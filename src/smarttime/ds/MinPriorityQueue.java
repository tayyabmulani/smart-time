package smarttime.ds;

public interface MinPriorityQueue<T> {
    void insert(T item);
    T findMin();
    T extractMin();
    boolean isEmpty();
    int size();
}