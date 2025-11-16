package smarttime.ds;

import java.util.List;

public interface GraphADT<V> {
    void addVertex(V v);
    void addEdge(V from, V to);
    List<V> getNeighbors(V v);
    boolean hasCycle();
}