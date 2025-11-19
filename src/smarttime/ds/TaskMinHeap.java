package smarttime.ds;

import smarttime.model.Task;
import java.time.LocalDate;

/**
 * Custom Min-Heap implementation for Task scheduling.
 * Ordering Rules:
 *  1. Lower difficulty = higher priority
 *  2. Earlier due date = higher priority
 *  3. Lower task ID = tie breaker
 */
public class TaskMinHeap implements MinPriorityQueue<Task> {

    private Task[] heap;
    private int size;

    public TaskMinHeap(int capacity) {
        heap = new Task[capacity];
        size = 0;
    }

    // ---------------------------
    // Public ADT Methods
    // ---------------------------

    @Override
    public void insert(Task task) {
        ensureCapacity();
        heap[size] = task;
        heapifyUp(size);
        size++;
    }

    @Override
    public Task findMin() {
        return size == 0 ? null : heap[0];
    }

    @Override
    public Task extractMin() {
        if (size == 0) return null;

        Task min = heap[0];
        heap[0] = heap[size - 1];
        size--;

        heapifyDown(0);
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

    // ---------------------------
    // Convenience wrappers used by TaskService
    // ---------------------------

    public Task peekMin() {
        return findMin();
    }

    public void add(Task t) {
        insert(t);
    }

    // ---------------------------
    // Internal Helpers
    // ---------------------------

    private void ensureCapacity() {
        if (size >= heap.length) {
            Task[] newArr = new Task[heap.length * 2];
            System.arraycopy(heap, 0, newArr, 0, heap.length);
            heap = newArr;
        }
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return (2 * i) + 1; }
    private int right(int i) { return (2 * i) + 2; }

    private void swap(int i, int j) {
        Task temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    /**
     * Comparator for tasks:
     * Lower difficulty = higher priority
     * Earlier due date = higher priority
     * Lower ID = tie breaker
     */
    private int compare(Task a, Task b) {
        if (a.getDifficulty() != b.getDifficulty()) {
            return a.getDifficulty() - b.getDifficulty(); // lower difficulty first
        }

        LocalDate da = a.getDueDate();
        LocalDate db = b.getDueDate();

        int dateCompare = da.compareTo(db);
        if (dateCompare != 0) {
            return dateCompare; // earlier date first
        }

        return a.getId() - b.getId(); // tie-breaker
    }

    // ---------------------------
    // Heapify logic
    // ---------------------------

    private void heapifyUp(int index) {
        while (index > 0) {
            int p = parent(index);
            if (compare(heap[index], heap[p]) < 0) {
                swap(index, p);
                index = p;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int left = left(index);
            int right = right(index);
            int smallest = index;

            if (left < size && compare(heap[left], heap[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare(heap[right], heap[smallest]) < 0) {
                smallest = right;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }
}
