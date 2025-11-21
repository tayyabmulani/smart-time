package smarttime.ds;

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
        if (tasks == null || tasks.size() <= 1) return;

        Comparator<Task> cmp = taskComparator();
        quickSort(tasks, 0, tasks.size() - 1, cmp);
    }

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

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> cmp) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cmp.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);  // custom swap
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        if (i == j) return;

        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}