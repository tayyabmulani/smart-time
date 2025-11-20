package smarttime.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import smarttime.model.Task;
import smarttime.model.TaskStatus;
import smarttime.service.TaskService;

public class TaskListPane extends VBox {

    private final TaskService taskService;
    private final ListView<Task> taskList;
    private final Button addTaskButton;
    private final Button markCompletedButton;

    private Runnable onAddTaskClicked;   // set by MainLayout
    private Runnable onTasksChanged;     // set by MainLayout

    public TaskListPane(TaskService taskService) {
        this.taskService = taskService;

        setSpacing(8);
        setPadding(new Insets(8));
        setPrefWidth(450);
        setMaxWidth(600);  
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Tasks");
        header.setStyle("-fx-font-weight: bold;");

        taskList = new ListView<>();
        VBox.setVgrow(taskList, Priority.ALWAYS);

        // Cell factory to show completed tasks differently
        taskList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String base = item.toString();
                    boolean isCompleted = item.getStatus() == TaskStatus.COMPLETED;
                    boolean isUnlocked = taskService.isTaskUnlocked(item) || isCompleted;

                    if (!isUnlocked) {
                        // Locked because prerequisites not done
                        setText("🔒 " + base);
                        setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
                    } else if (isCompleted) {
                        setText("✓ " + base);
                        setStyle("-fx-text-fill: #777777;");
                    } else {
                        setText(base);
                        setStyle(""); // default
                    }
                }
            }
        });

        addTaskButton = new Button("+ Add Task");
        addTaskButton.setMaxWidth(Double.MAX_VALUE);
        addTaskButton.setOnAction(e -> {
            if (onAddTaskClicked != null) {
                onAddTaskClicked.run();
            }
        });

        markCompletedButton = new Button("Mark Completed");
        markCompletedButton.setMaxWidth(Double.MAX_VALUE);
        markCompletedButton.setOnAction(e -> handleMarkCompleted());

        getChildren().addAll(header, taskList, addTaskButton, markCompletedButton);

        // Load initial tasks
        refresh();
    }

    /**
     * Reload tasks from TaskService into the ListView.
     */
    public void refresh() {
        taskList.getItems().setAll(taskService.getAllTasksSorted());
    }

    private void handleMarkCompleted() {
        Task selected = taskList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return; // nothing selected
        }

        // Do not allow completing locked tasks
        if (!taskService.isTaskUnlocked(selected)) {
            // (optional) you could add a dialog later; for now just ignore.
            return;
        }

        taskService.markTaskCompleted(selected);

        // Refresh UI to show the new status
        refresh();

        if (onTasksChanged != null) {
            onTasksChanged.run();
        }
    }

    public ListView<Task> getTaskListView() {
        return taskList;
    }

    public void setOnAddTaskClicked(Runnable onAddTaskClicked) {
        this.onAddTaskClicked = onAddTaskClicked;
    }

    public void setOnTasksChanged(Runnable onTasksChanged) {
        this.onTasksChanged = onTasksChanged;
    }
}