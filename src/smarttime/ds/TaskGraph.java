package smarttime.ds;

import java.util.*;

import smarttime.model.Task;

/**
 * Directed graph of task dependencies.
 * Edge: prerequisite -> dependent.
 *
 * Uses:
 *  - hasCycle(): recursive DFS to detect cycles
 *  - canStart(): check if all prerequisites of a task are completed
 */
public class TaskGraph implements GraphInterface<Task> {

    // from -> list of dependents
    private final Map<Task, List<Task>> adj = new HashMap<>();
    // task -> list of prerequisites
    private final Map<Task, List<Task>> prereqMap = new HashMap<>();

    @Override
    public void addVertex(Task v) {
        adj.putIfAbsent(v, new ArrayList<>());
        prereqMap.putIfAbsent(v, new ArrayList<>());
    }

    @Override
    public void addEdge(Task from, Task to) {
        addVertex(from);
        addVertex(to);

        // prevent self dependency A → A
        if (from == to) {
            throw new IllegalArgumentException("A task cannot depend on itself.");
        }

        // temporarily add edge
        adj.get(from).add(to);
        prereqMap.get(to).add(from);

        // check if cycle is formed
        if (hasCycle()) {
            // rollback
            adj.get(from).remove(to);
            prereqMap.get(to).remove(from);

            throw new IllegalArgumentException("Adding this dependency creates a cycle.");
        }
    }

    /**
     * Convenience: ensure a task exists in the graph.
     */
    public void addTask(Task task) {
        addVertex(task);
    }

    /**
     * Convenience: add a dependency edge prerequisite -> dependent.
     */
    public void addDependency(Task prerequisite, Task dependent) {
        addEdge(prerequisite, dependent);
    }

    @Override
    public List<Task> getNeighbors(Task v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    /**
     * Direct prerequisites of a given task.
     */
    public List<Task> getPrerequisites(Task v) {
        return prereqMap.getOrDefault(v, Collections.emptyList());
    }

    /**
     * Recursive DFS-based cycle detection.
     * Colors:
     *   0 = unvisited
     *   1 = visiting
     *   2 = done
     */
    @Override
    public boolean hasCycle() {
        Map<Task, Integer> color = new HashMap<>();

        for (Task t : adj.keySet()) {
            if (dfsCycle(t, color)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsCycle(Task v, Map<Task, Integer> color) {
        int c = color.getOrDefault(v, 0);
        if (c == 1) {
            // back edge → cycle
            return true;
        }
        if (c == 2) {
            return false;
        }

        color.put(v, 1); // visiting
        for (Task nb : getNeighbors(v)) {
            if (dfsCycle(nb, color)) {
                return true;
            }
        }
        color.put(v, 2); // done
        return false;
    }

    /**
     * Check if a task can start, given a set of completed tasks.
     * Returns true if ALL prerequisites are in the completed set.
     */
    public boolean canStart(Task t, Set<Task> completed) {
        List<Task> prereqs = prereqMap.getOrDefault(t, Collections.emptyList());
        if (prereqs.isEmpty()) {
            return true;
        }
        for (Task p : prereqs) {
            if (!completed.contains(p)) {
                return false;
            }
        }
        return true;
    }
}