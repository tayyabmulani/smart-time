package smarttime.ds;

import smarttime.model.Task;
import java.time.LocalDate;

/**
 * Array-based min-heap for Task, implementing our custom MinPriorityQueue.
 *
 * Priority rules:
 *  1) Earlier due date = higher priority
 *  2) If same due date: lower difficulty first
 *  3) If same difficulty: smaller estimatedMinutes first
 */
public class TaskMinHeap implements QueueInterface<Task> {

    private Task[] heap;
    private int size;

    public TaskMinHeap(int capacity) {
        heap = new Task[capacity];
        size = 0;
    }

    @Override
    public void insert(Task task) {
        ensureCapacity();
        heap[size] = task;
        heapifyUp(size);
        size++;
    }

    // Convenience method so TaskService can call heap.add(task)
    public void add(Task task) {
        insert(task);
    }

    @Override
    public Task findMin() {
        return size == 0 ? null : heap[0];
    }

    public Task peekMin() {
        return findMin();
    }

    @Override
    public Task extractMin() {
        if (size == 0) return null;

        Task min = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            heapifyDown(0);
        }

        return min;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size == heap.length) {
            Task[] newHeap = new Task[heap.length * 2];
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;
        }
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return 2 * index + 1;
    }

    private int rightChild(int index) {
        return 2 * index + 2;
    }

    private void heapifyUp(int index) {
        int current = index;
        while (current > 0) {
            int parentIndex = parent(current);
            if (compare(heap[current], heap[parentIndex]) < 0) {
                swap(current, parentIndex);
                current = parentIndex;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int current = index;
        while (true) {
            int left = leftChild(current);
            int right = rightChild(current);
            int smallest = current;

            if (left < size && compare(heap[left], heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare(heap[right], heap[smallest]) < 0) {
                smallest = right;
            }

            if (smallest != current) {
                swap(current, smallest);
                current = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Task tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    /**
     * Compare two tasks according to our priority rules.
     * Returns negative if a has higher priority than b.
     */
    private int compare(Task a, Task b) {
        if (a == null && b == null) return 0;
        if (a == null) return 1;
        if (b == null) return -1;

        // 1) earlier due date first
        LocalDate da = a.getDueDate();
        LocalDate db = b.getDueDate();
        int cmp = da.compareTo(db);
        if (cmp != 0) return cmp;

        // 2) lower difficulty first
        cmp = Integer.compare(a.getDifficulty(), b.getDifficulty());
        if (cmp != 0) return cmp;

        // 3) smaller estimatedMinutes first
        return Integer.compare(a.getEstimatedMinutes(), b.getEstimatedMinutes());
    }
    
    /** Clear the heap completely (used when rebuilding heap). */
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

}