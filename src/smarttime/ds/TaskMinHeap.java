package smarttime.ds;

import smarttime.model.Task;

public class TaskMinHeap implements MinPriorityQueue<Task> {

    private Task[] heap;
    private int size;

    public TaskMinHeap(int capacity) {
        heap = new Task[capacity];
        size = 0;
    }

    @Override
    public void insert(Task task) {
        // TODO:  
    }

    @Override
    public Task findMin() {
        return size == 0 ? null : heap[0];
    }

    @Override
    public Task extractMin() {
        // TODO: 
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

	public Task peekMin() {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(Task task) {
		// TODO Auto-generated method stub
		
	}

    // private helpers: parent, left, right, swap, compare(Task a, Task b)
}