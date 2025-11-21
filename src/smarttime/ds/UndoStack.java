package smarttime.ds;

/**
 * Simple stack wrapper for undo operations, built on our own StackADT.
 */
public class UndoStack<T> {

    private final StackInterface<T> stack;

    public UndoStack() {
        // default initial capacity; ArrayStack will grow if needed
        this.stack = new ArrayStack<>(16);
    }

    public UndoStack(int initialCapacity) {
        this.stack = new ArrayStack<>(initialCapacity);
    }

    public void push(T action) {
        stack.push(action);
    }

    public T pop() {
        return stack.pop();
    }

    public T peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }
}