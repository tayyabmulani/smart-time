package smarttime.ds;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Simple stack wrapper for undo operations.
 */
public class UndoStack<T> {

    private final Deque<T> stack = new ArrayDeque<>();

    public void push(T action) {
        stack.push(action);
    }

    public T pop() {
        return stack.isEmpty() ? null : stack.pop();
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