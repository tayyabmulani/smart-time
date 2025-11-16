package smarttime.service;

import java.util.ArrayList;
import java.util.List;

import smarttime.ds.TaskGraph;
import smarttime.ds.TaskMinHeap;
import smarttime.model.Task;

/**
 * Glue between UI and DS.
 */
public class TaskService {

    private final List<Task> allTasks = new ArrayList<>();
    private final TaskMinHeap heap;
    private final TaskGraph graph;

    public TaskService(TaskMinHeap heap, TaskGraph graph) {
        this.heap = heap;
        this.graph = graph;
    }

    public void addTask(Task task) {
        allTasks.add(task);
        heap.add(task);
        graph.addTask(task);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(allTasks);
    }

    public Task getNextRecommendedTask() {
        return heap.peekMin();
    }
}