package smarttime.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import smarttime.ds.TaskGraph;
import smarttime.ds.TaskMinHeap;
import smarttime.ds.UndoStack;
import smarttime.ds.TaskSorter;
import smarttime.model.Task;
import smarttime.model.TaskStatus;
import smarttime.model.UndoAction;
import smarttime.model.UndoAction.ActionType;

/**
 * Glue between UI and DS.
 * All mutations must go through this class so undo works correctly.
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
    
    // ADD TASK
    public void addTask(Task task) {
        allTasks.add(task);
        heap.insert(task);
        graph.addTask(task);

        undoStack.push(new UndoAction(ActionType.ADD_TASK, task));
    }

    // MARK COMPLETED
    public void markTaskCompleted(Task task) {
        if (task == null) return;

        TaskStatus previous = task.getStatus();
        if (previous == TaskStatus.COMPLETED) return;

        task.setStatus(TaskStatus.COMPLETED);
        undoStack.push(new UndoAction(ActionType.UPDATE_STATUS, task, previous));

        rebuildHeap();
    }

    // EDIT TASK
    public void updateTask(Task task,
                           String newTitle,
                           String newCourse,
                           LocalDate newDueDate,
                           int newMinutes,
                           int newDifficulty) {

        if (task == null) return;

        // snapshot before edit (for undo)
        Task snapshot = new Task(
                task.getId(),
                task.getTitle(),
                task.getCourse(),
                task.getDueDate(),
                task.getEstimatedMinutes(),
                task.getDifficulty()
        );
        snapshot.setStatus(task.getStatus());

        // apply new values
        task.setTitle(newTitle);
        task.setCourse(newCourse);
        task.setDueDate(newDueDate);
        task.setEstimatedMinutes(newMinutes);
        task.setDifficulty(newDifficulty);

        rebuildHeap();

        undoStack.push(new UndoAction(ActionType.UPDATE_TASK_DETAILS, task, snapshot));
    }

    // DELETE TASK
    public void deleteTask(Task task) {
        if (task == null) return;

        // Save for undo
        undoStack.push(new UndoAction(ActionType.DELETE_TASK, task));

        removeTaskInternal(task);
        rebuildHeap();
    }

    // INTERNAL REMOVE
    private void removeTaskInternal(Task task) {
        allTasks.remove(task);
        // when graph supports remove: graph.removeTask(task);
    }
    
    // UNDO
    public void undoLastAction() {
        if (undoStack.isEmpty()) return;

        UndoAction action = undoStack.pop();
        Task task = action.getTask();

        switch (action.getType()) {

            case ADD_TASK:
                removeTaskInternal(task);
                rebuildHeap();
                break;

            case UPDATE_STATUS:
                task.setStatus(action.getPreviousStatus());
                rebuildHeap();
                break;

            case UPDATE_TASK_DETAILS:
                Task snapshot = action.getSnapshot();
                if (snapshot != null) {
                    task.setTitle(snapshot.getTitle());
                    task.setCourse(snapshot.getCourse());
                    task.setDueDate(snapshot.getDueDate());
                    task.setEstimatedMinutes(snapshot.getEstimatedMinutes());
                    task.setDifficulty(snapshot.getDifficulty());
                    task.setStatus(snapshot.getStatus());
                }
                rebuildHeap();
                break;

            case DELETE_TASK:
                allTasks.add(task);
                graph.addTask(task);
                rebuildHeap();
                break;
        }
    }

    // REBUILD HEAP
    private void rebuildHeap() {
        heap.clear();
        for (Task t : allTasks) {
            heap.insert(t);
        }
    }
    // ACCESSORS
    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks);
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public Task getNextRecommendedTask() {
        List<Task> buffer = new ArrayList<>();
        Task candidate = heap.extractMin();

        while (candidate != null &&
              (candidate.getStatus() == TaskStatus.COMPLETED ||
                !allTasks.contains(candidate))) {
            buffer.add(candidate);
            candidate = heap.extractMin();
        }

        for (Task t : buffer) heap.insert(t);
        if (candidate != null) heap.insert(candidate);

        return candidate;
    }

    // GRAPH FUNCTIONS
    public void addDependency(Task prerequisite, Task dependent) {
        graph.addDependency(prerequisite, dependent);
    }

    public boolean isTaskUnlocked(Task task) {
        Set<Task> completed = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toSet());
        return graph.canStart(task, completed);
    }

    public List<Task> getPrerequisites(Task task) {
        return graph.getPrerequisites(task);
    }

    // SORTING WITH CUSTOM QUICKSORT
    public List<Task> getAllTasksSorted() {
        List<Task> copy = new ArrayList<>(allTasks);
        TaskSorter.quickSortTasks(copy);
        return copy;
    }

    public int getNextId() {
        return allTasks.size() + 1;
    }
    
    // SORT BY DUE DATE
    public List<Task> getTasksSortedByDueDate() {
        List<Task> copy = new ArrayList<>(allTasks);
        TaskSorter.sortByDueDate(copy);
        return copy;
    }

    // SORT BY DIFFICULTY
    public List<Task> getTasksSortedByDifficulty() {
        List<Task> copy = new ArrayList<>(allTasks);
        TaskSorter.sortByDifficulty(copy);
        return copy;
    }

}