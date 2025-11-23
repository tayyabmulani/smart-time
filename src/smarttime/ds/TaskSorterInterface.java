package smarttime.ds;

import java.util.Comparator;
import java.util.List;
import smarttime.model.Task;

public interface TaskSorterInterface {

    /**
     * Sorts the given list of tasks using the provided comparator.
     * Implementations define the sorting algorithm (QuickSort, MergeSort, etc.)
     */
    void sort(List<Task> tasks, Comparator<Task> comparator);
}
