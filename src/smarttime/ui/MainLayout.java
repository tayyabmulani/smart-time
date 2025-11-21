package smarttime.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import smarttime.model.Task;
import smarttime.service.TaskService;

public class MainLayout extends BorderPane {

    private final TaskService taskService;

    private final TaskListPane taskListPane;
    private final TodayOverviewPane todayOverviewPane;
    private final TaskFormPane taskFormPane;
    private final StatusBar statusBar;

    // Track which task is currently selected in the list
    private Task selectedTask;

    public MainLayout(TaskService taskService) {
        this.taskService = taskService;

        // left: task list
        taskListPane = new TaskListPane(taskService);

        // center: overview
        todayOverviewPane = new TodayOverviewPane(taskService);

        // bottom: status bar (with Undo)
        statusBar = new StatusBar(taskService, () -> {
            // After undo, refresh UI parts that depend on tasks
            taskListPane.refresh();
            todayOverviewPane.refresh();
        });

        // center alt: task form (we reuse this for both ADD and EDIT)
        taskFormPane = new TaskFormPane(taskService, () -> {
            // called after Save / Cancel from form
            taskListPane.refresh();
            todayOverviewPane.refresh();
            statusBar.updateUndoState();
            showOverview();
        });

        // When user changes selection in the task list,
        // To remember the selected task and show its prerequisites in the overview
        taskListPane.getTaskListView()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldTask, newTask) -> {
                    selectedTask = newTask;
                    todayOverviewPane.setSelectedTask(newTask);
                });

        // header
        VBox header = new VBox(4);
        Label title = new Label("SmartTime – Study & Time Planner");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Using custom ADTs (Stack, Heap, Graph, QuickSort) under the hood.");
        subtitle.setStyle("-fx-text-fill: #555555;");
        header.getChildren().addAll(title, subtitle);
        header.setPadding(new Insets(8));
        header.setStyle("-fx-background-color: #f5f5f5;");

        setPadding(new Insets(8));
        setTop(header);
        setLeft(taskListPane);
        setCenter(todayOverviewPane);
        setBottom(statusBar);

        BorderPane.setMargin(taskListPane, new Insets(8, 8, 8, 0));
        BorderPane.setMargin(todayOverviewPane, new Insets(8, 0, 8, 8));
        BorderPane.setMargin(taskFormPane, new Insets(8, 0, 8, 8));

        // --- Wire buttons / actions from TaskListPane ---

        // "Add Task" button
        taskListPane.setOnAddTaskClicked(this::showAddTaskForm);

        // "Edit Task" button / action in TaskListPane
        taskListPane.setOnEditTaskClicked(() -> {
            if (selectedTask != null) {
                showEditTaskForm(selectedTask);
            }
        });

        // "Delete Task" button / action in TaskListPane
        taskListPane.setOnDeleteTaskClicked(() -> {
            if (selectedTask != null) {
                taskService.deleteTask(selectedTask);
                taskListPane.refresh();
                todayOverviewPane.refresh();
                statusBar.updateUndoState();
                statusBar.setStatusText("Task deleted.");
                selectedTask = null;
                todayOverviewPane.setSelectedTask(null);
            }
        });

        // Whenever tasks change (add/edit/delete/complete), refresh
        taskListPane.setOnTasksChanged(() -> {
            statusBar.updateUndoState();
            todayOverviewPane.refresh();
        });

        // Initial Undo button state (handles any preloaded tasks)
        statusBar.updateUndoState();
    }


    /** Show form in ADD mode. */
    private void showAddTaskForm() {
        taskFormPane.startAddMode();
        setCenter(taskFormPane);
        statusBar.setStatusText("Adding a new task...");
    }

    /** Show form in EDIT mode for the given task. */
    private void showEditTaskForm(Task task) {
        taskFormPane.startEditMode(task);
        setCenter(taskFormPane);
        statusBar.setStatusText("Editing task...");
    }

    /** Go back to overview (center card with today's stats + recommendation). */
    private void showOverview() {
        setCenter(todayOverviewPane);
        statusBar.setStatusText("Ready");
    }
}