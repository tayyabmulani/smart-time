package smarttime.ds;

@SuppressWarnings("unchecked")
public class ArrayStack<T> implements StackInterface<T> {

    private T[] stack;
    private int top; // index of next free slot
    private static final int DEFAULT_CAPACITY = 50;
    private static final int MAX_CAPACITY = 10000;
    
    public ArrayStack() {
    	this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int capacity) {
    	@SuppressWarnings("unchecked")
        T[] tempStack = (T[]) new Object[capacity];
    	stack = tempStack;
        top = 0;
    }

    @Override
    public void push(T item) {
        if (top == stack.length) {
            // grow array (simple doubling)
            T[] newData = (T[]) new Object[stack.length * 2];
            System.arraycopy(stack, 0, newData, 0, stack.length);
            stack = newData;
        }
        stack[top++] = item;
    }

    @Override
    public T pop() {
        if (isEmpty()) return null;
        T item = stack[--top];
        stack[top] = null; 
        return item;
    }

    @Override
    public T peek() {
        if (isEmpty()) return null;
        return stack[top - 1];
    }

    @Override
    public boolean isEmpty() {
        return top == 0;
    }

    @Override
    public int size() {
        return top;
    }
}