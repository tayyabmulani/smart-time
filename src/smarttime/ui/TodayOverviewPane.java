package smarttime.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import smarttime.model.Task;
import smarttime.model.TaskStatus;
import smarttime.service.TaskService;

public class TodayOverviewPane extends VBox {

    private final TaskService taskService;

    private final Label todayLabel;
    private final Label completedLabel;
    private final Label pendingLabel;
    private final Label recommendedLabel;

    private final Label selectedTaskLabel;
    private final Label prerequisitesLabel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d");

    public TodayOverviewPane(TaskService taskService) {
        this.taskService = taskService;

        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Today's Tasks");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        todayLabel = new Label();
        completedLabel = new Label();
        pendingLabel = new Label();

        recommendedLabel = new Label();
        recommendedLabel.setWrapText(true);
        recommendedLabel.setMaxWidth(Double.MAX_VALUE);

        selectedTaskLabel = new Label("Selected task: (none)");
        prerequisitesLabel = new Label("Prerequisites: (none)");

        getChildren().addAll(header, todayLabel, completedLabel, pendingLabel, recommendedLabel, selectedTaskLabel, prerequisitesLabel);

        refresh();
    }

    public void refresh() {
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskService.getAllTasks();

        // Filter only today's tasks
        List<Task> todayTasks = tasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isEqual(today))
                .toList();

        int total = todayTasks.size();
        long completed = todayTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        int pending = total - (int) completed;

        todayLabel.setText("Today's tasks: " + total);
        completedLabel.setText("Completed today: " + completed);
        pendingLabel.setText("Pending today: " + pending);

        Task recommended = taskService.getNextRecommendedTask();

        if (recommended == null || recommended.getDueDate() == null || !recommended.getDueDate().isEqual(today)) {
            recommendedLabel.setText("Recommended next (today): (none)");
            recommendedLabel.setTooltip(null);
        } else {
            StringBuilder sb = new StringBuilder("Recommended today: ");
            sb.append(recommended.getTitle());

            if (recommended.getCourse() != null && !recommended.getCourse().isEmpty()) {
                sb.append(" · ").append(recommended.getCourse());
            }
            sb.append(" · due ").append(recommended.getDueDate().format(dateFormatter));
            sb.append(" · difficulty ").append(recommended.getDifficulty());

            String fullText = sb.toString();
            recommendedLabel.setText(fullText);
            recommendedLabel.setTooltip(new Tooltip(fullText));
        }
    }

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