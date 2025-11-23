package smarttime.ds;

import java.util.Comparator;
import java.util.List;
import smarttime.model.Task;

/**
 * Custom QuickSort implementation for Tasks.
 * Implements the Sorting ADT (TaskSorterInterface).
 */
public class TaskSorter implements TaskSorterInterface {

    @Override
    public void sort(List<Task> tasks, Comparator<Task> comparator) {
        if (tasks == null || tasks.size() <= 1) return;
        quickSort(tasks, 0, tasks.size() - 1, comparator);
    }


    public static void quickSortTasks(List<Task> tasks) {
        new TaskSorter().sort(tasks, taskComparator());
    }

    public static void sortByDueDate(List<Task> tasks) {
        Comparator<Task> cmp = Comparator.comparing(
                Task::getDueDate, Comparator.nullsLast((d1, d2) -> d1.compareTo(d2))
        );
        new TaskSorter().sort(tasks, cmp);
    }

    public static void sortByDifficulty(List<Task> tasks) {
        Comparator<Task> cmp = Comparator.comparingInt(Task::getDifficulty);
        new TaskSorter().sort(tasks, cmp);
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
                swap(list, i, j);
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