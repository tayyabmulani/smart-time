package smarttime.ds;

@SuppressWarnings("unchecked")
public class ArrayStack<T> implements StackADT<T> {

    private T[] data;
    private int top; // index of next free slot

    public ArrayStack(int capacity) {
        data = (T[]) new Object[capacity];
        top = 0;
    }

    @Override
    public void push(T item) {
        if (top == data.length) {
            // grow array (simple doubling)
            T[] newData = (T[]) new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[top++] = item;
    }

    @Override
    public T pop() {
        if (isEmpty()) return null;
        T item = data[--top];
        data[top] = null; // avoid memory leak
        return item;
    }

    @Override
    public T peek() {
        if (isEmpty()) return null;
        return data[top - 1];
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