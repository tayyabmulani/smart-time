package smarttime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import smarttime.ds.TaskGraph;
import smarttime.ds.TaskMinHeap;
import smarttime.ds.UndoStack;
import smarttime.model.Task;
import smarttime.model.TaskStatus;
import smarttime.model.UndoAction;
import smarttime.model.UndoAction.ActionType;

import java.util.Collections;
import java.util.Comparator;

/**
 * Glue between UI and DS.
 *
 * All mutations to tasks should go through this class so that
 * we can record them on an UndoStack and support undo.
 */
public class TaskService {

    private final List<Task> allTasks = new ArrayList<>();
    private final TaskMinHeap heap;
    private final TaskGraph graph;
    private final UndoStack<UndoAction> undoStack = new UndoStack<>();

    public TaskService(TaskMinHeap heap, TaskGraph graph) {
        this.heap = heap;
        this.graph = graph;
    }

    /**
     * Add a new task to the system.
     * This records an ADD_TASK undo action.
     */
    public void addTask(Task task) {
        allTasks.add(task);
        heap.add(task);
        graph.addTask(task);

        // record this so we can undo the addition
        undoStack.push(new UndoAction(ActionType.ADD_TASK, task, null));
    }

    /**
     * Mark a task as completed.
     * This records an UPDATE_STATUS undo action.
     */
    public void markTaskCompleted(Task task) {
        if (task == null) return;

        TaskStatus previous = task.getStatus();
        if (previous == TaskStatus.COMPLETED) {
            // nothing to do
            return;
        }

        task.setStatus(TaskStatus.COMPLETED);

        // later we can also update heap/graph if needed
        undoStack.push(new UndoAction(ActionType.UPDATE_STATUS, task, previous));
    }

    /**
     * Undo the last action if possible.
     */
    public void undoLastAction() {
        if (undoStack.isEmpty()) {
            return;
        }

        UndoAction action = undoStack.pop();
        Task task = action.getTask();

        switch (action.getType()) {
            case ADD_TASK:
                // undo: remove the task we just added
                removeTaskInternal(task);
                break;

            case UPDATE_STATUS:
                // undo: restore previous status
                TaskStatus prevStatus = action.getPreviousStatus();
                if (prevStatus != null && task != null) {
                    task.setStatus(prevStatus);
                }
                break;
        }
    }

    /**
     * Internal helper to remove a task from all structures.
     * NOTE: heap/graph removal can be refined once those APIs exist.
     */
    private void removeTaskInternal(Task task) {
        if (task == null) return;

        allTasks.remove(task);
        // TODO: when Aditya/Bharat add support for removal,
        // also remove from heap and graph.
        // e.g., heap.remove(task); graph.removeTask(task);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks);
    }

    public Task getNextRecommendedTask() {
        // Use the heap, but skip completed tasks.
        // We temporarily pop items and put them back to keep the heap consistent.
        List<Task> buffer = new ArrayList<>();

        Task candidate = heap.extractMin();
        while (candidate != null && candidate.getStatus() == TaskStatus.COMPLETED) {
            buffer.add(candidate);
            candidate = heap.extractMin();
        }

        // Put everything back into the heap
        for (Task t : buffer) {
            heap.insert(t);
        }
        if (candidate != null) {
            heap.insert(candidate);
        }

        return candidate;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Add a prerequisite relationship: prerequisite must be completed
     * before dependent can start.
     */
    public void addDependency(Task prerequisite, Task dependent) {
        graph.addDependency(prerequisite, dependent);
    }

    /**
     * Returns true if the task is allowed to start:
     * either it has no prerequisites or all of them are completed.
     */
    public boolean isTaskUnlocked(Task task) {
        Set<Task> completed = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toSet());

        return graph.canStart(task, completed);
    }
    
    /**
     * Get direct prerequisites for a given task from the graph.
     */
    public List<Task> getPrerequisites(Task task) {
        return graph.getPrerequisites(task);
    }
    
    /**
     * Returns tasks sorted by:
     *  1) due date (earliest first)
     *  2) difficulty (easier first)
     *  3) title (alphabetical)
     */
    public List<Task> getAllTasksSorted() {
        List<Task> copy = new ArrayList<>(allTasks); // or whatever your internal list is called

        Collections.sort(copy, new Comparator<Task>() {
            @Override
            public int compare(Task a, Task b) {
                // 1. due date
                int cmp = a.getDueDate().compareTo(b.getDueDate());
                if (cmp != 0) return cmp;

                // 2. difficulty
                cmp = Integer.compare(a.getDifficulty(), b.getDifficulty());
                if (cmp != 0) return cmp;

                // 3. title
                return a.getTitle().compareToIgnoreCase(b.getTitle());
            }
        });

        return copy;
    }
}