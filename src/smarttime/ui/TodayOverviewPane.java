package smarttime.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import smarttime.model.Task;
import smarttime.model.TaskStatus;
import smarttime.service.TaskService;

public class TodayOverviewPane extends VBox {

    private final TaskService taskService;

    private final Label totalLabel;
    private final Label completedLabel;
    private final Label pendingLabel;
    private final Label recommendedLabel;

    // NEW:
    private final Label selectedTaskLabel;
    private final Label prerequisitesLabel;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("MMM d");

    public TodayOverviewPane(TaskService taskService) {
        this.taskService = taskService;

        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Today’s Overview");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        totalLabel = new Label();
        completedLabel = new Label();
        pendingLabel = new Label();
        recommendedLabel = new Label();

        selectedTaskLabel = new Label("Selected task: (none)");
        prerequisitesLabel = new Label("Prerequisites: (none)");

        getChildren().addAll(
                header,
                totalLabel,
                completedLabel,
                pendingLabel,
                recommendedLabel,
                selectedTaskLabel,
                prerequisitesLabel
        );

        refresh();
    }

    /**
     * Recompute counts and refresh the recommended task display.
     * Call this whenever tasks are added, completed, or undone.
     */
    public void refresh() {
        List<Task> tasks = taskService.getAllTasks();

        int total = tasks.size();
        int completed = 0;
        for (Task t : tasks) {
            if (t.getStatus() == TaskStatus.COMPLETED) {
                completed++;
            }
        }
        int pending = total - completed;

        totalLabel.setText("Total tasks: " + total);
        completedLabel.setText("Completed: " + completed);
        pendingLabel.setText("Pending: " + pending);

        Task recommended = taskService.getNextRecommendedTask();
        if (recommended == null) {
            recommendedLabel.setText("Recommended next: (none)");
        } else {
            String text = "Recommended next: " + recommended.getTitle()
                    + " · due " + recommended.getDueDate().format(dateFormatter)
                    + " · difficulty " + recommended.getDifficulty();
            recommendedLabel.setText(text);
        }
        // NOTE: we do NOT touch selectedTaskLabel/prerequisitesLabel here;
        // those are controlled by setSelectedTask(...)
    }

    /**
     * Called when the user selects a task in the list.
     * Shows its title and its direct prerequisites.
     */
    public void setSelectedTask(Task task) {
        if (task == null) {
            selectedTaskLabel.setText("Selected task: (none)");
            prerequisitesLabel.setText("Prerequisites: (none)");
            return;
        }

        selectedTaskLabel.setText("Selected task: " + task.getTitle());

        List<Task> prereqs = taskService.getPrerequisites(task);
        if (prereqs == null || prereqs.isEmpty()) {
            prerequisitesLabel.setText("Prerequisites: (none)");
        } else {
            StringBuilder sb = new StringBuilder("Prerequisites: ");
            for (int i = 0; i < prereqs.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(prereqs.get(i).getTitle());
            }
            prerequisitesLabel.setText(sb.toString());
        }
    }
}