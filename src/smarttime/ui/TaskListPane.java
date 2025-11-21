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
    private final Button editTaskButton;
    private final Button deleteTaskButton;

    private Runnable onAddTaskClicked;
    private Runnable onEditTaskClicked;
    private Runnable onDeleteTaskClicked;
    private Runnable onTasksChanged;

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

        // Cell formatting
        taskList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean isCompleted = item.getStatus() == TaskStatus.COMPLETED;
                    boolean isUnlocked = taskService.isTaskUnlocked(item) || isCompleted;

                    if (!isUnlocked) {
                        setText("🔒 " + item);
                        setStyle("-fx-text-fill: #999999; -fx-font-style: italic;");
                    } else if (isCompleted) {
                        setText("✓ " + item);
                        setStyle("-fx-text-fill: #777777;");
                    } else {
                        setText(item.toString());
                        setStyle("");
                    }
                }
            }
        });

        // Buttons: Add Button
        addTaskButton = new Button("+ Add Task");
        addTaskButton.setMaxWidth(Double.MAX_VALUE);
        addTaskButton.setOnAction(e -> {
            if (onAddTaskClicked != null) onAddTaskClicked.run();
        });

        markCompletedButton = new Button("Mark Completed");
        markCompletedButton.setMaxWidth(Double.MAX_VALUE);
        markCompletedButton.setOnAction(e -> handleMarkCompleted());

        // Buttons: Edit button
        editTaskButton = new Button("Edit Task");
        editTaskButton.setMaxWidth(Double.MAX_VALUE);
        editTaskButton.setOnAction(e -> {
            if (onEditTaskClicked != null) onEditTaskClicked.run();
        });

        // Buttons: Delete button
        deleteTaskButton = new Button("Delete Task");
        deleteTaskButton.setMaxWidth(Double.MAX_VALUE);
        deleteTaskButton.setStyle("-fx-background-color: #ffdddd;");
        deleteTaskButton.setOnAction(e -> {
            if (onDeleteTaskClicked != null) onDeleteTaskClicked.run();
        });

        getChildren().addAll(
                header,
                taskList,
                addTaskButton,
                markCompletedButton,
                editTaskButton,
                deleteTaskButton
        );

        refresh();
    }

    public void refresh() {
        taskList.getItems().setAll(taskService.getAllTasksSorted());
    }

    private void handleMarkCompleted() {
        Task selected = taskList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (!taskService.isTaskUnlocked(selected)) return;

        taskService.markTaskCompleted(selected);

        refresh();
        if (onTasksChanged != null) onTasksChanged.run();
    }

    // Getters for MainLayout
    public ListView<Task> getTaskListView() {
        return taskList;
    }

    public void setOnAddTaskClicked(Runnable r) {
        this.onAddTaskClicked = r;
    }

    public void setOnEditTaskClicked(Runnable r) {
        this.onEditTaskClicked = r;
    }

    public void setOnDeleteTaskClicked(Runnable r) {
        this.onDeleteTaskClicked = r;
    }

    public void setOnTasksChanged(Runnable r) {
        this.onTasksChanged = r;
    }
}