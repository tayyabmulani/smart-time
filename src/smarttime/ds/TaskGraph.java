package smarttime.ds;

import java.util.*;

import smarttime.model.Task;

public class TaskGraph implements GraphADT<Task> {

    // adjacency list: Task -> list of dependents
    private final Map<Task, List<Task>> adj = new HashMap<>();

    @Override
    public void addVertex(Task v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    @Override
    public void addEdge(Task from, Task to) {
        adj.putIfAbsent(from, new ArrayList<>());
        adj.putIfAbsent(to, new ArrayList<>());
        adj.get(from).add(to);
    }

    @Override
    public List<Task> getNeighbors(Task v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    @Override
    public boolean hasCycle() {
        // TODO: implement recursive DFS for cycle detection
        return false;
    }

    public boolean canStart(Task t, Set<Task> completed) {
        // TODO: implement: check if all prerequisites are completed
        return true;
    }

	public void addTask(Task task) {
		// TODO Auto-generated method stub
		
	}
}