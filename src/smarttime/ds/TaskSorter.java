package smarttime.ds;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import smarttime.model.Task;

/**
 * TaskSorter
 * Custom QuickSort implementation for Tasks.
 */
public class TaskSorter {

    /**
     * Sorts the given list of tasks in-place using QuickSort.
     *
     * Order:
     *   1. Earlier due date first
     *   2. If same due date -> lower difficulty first
     *   3. If same difficulty -> smaller estimated minutes first
     *   4. Then alphabetical by title (case-insensitive)
     */
    public static void quickSortTasks(List<Task> tasks) {
        if (tasks == null || tasks.size() <= 1) {
            return; // nothing to sort
        }
        Comparator<Task> cmp = taskComparator();
        quickSort(tasks, 0, tasks.size() - 1, cmp);
    }

    // ---------- Internal QuickSort implementation ----------

    private static Comparator<Task> taskComparator() {
        return Comparator
                .comparing(Task::getDueDate)
                .thenComparingInt(Task::getDifficulty)
                .thenComparingInt(Task::getEstimatedMinutes)
                .thenComparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> cmp) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, cmp);
            quickSort(list, low, pivotIndex - 1, cmp);
            quickSort(list, pivotIndex + 1, high, cmp);
        }
    }

    /**
     * Lomuto partition scheme.
     * Picks the last element as pivot, partitions the list so that:
     * - elements <= pivot are on the left
     * - elements > pivot are on the right
     */
    private static <T> int partition(List<T> list, int low, int high, Comparator<T> cmp) {
        T pivot = list.get(high);
        int i = low - 1; // index of smaller element

        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}