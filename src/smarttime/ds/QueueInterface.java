package smarttime.ds;

/**
 * Generic ADT for a Min-Priority Queue.
 * The minimum element should always be accessible in O(1),
 * while insert/extract should run in O(log n).
 */
public interface QueueInterface<T> {
    void insert(T item);
    T findMin();
    T extractMin();
    boolean isEmpty();
    int size();
}
