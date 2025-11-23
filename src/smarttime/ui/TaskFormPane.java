package smarttime.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import smarttime.model.Task;
import smarttime.service.TaskService;

public class TaskFormPane extends VBox {

    private final TaskService taskService;
    private final Runnable onDone; 

    private final Label headerLabel;
    private final TextField titleField;
    private final TextField courseField;
    private final TextField dueDateField;
    private final TextField minutesField;
    private final TextField difficultyField;
    private final Label errorLabel;

    private final ListView<Task> prereqListView;

    private final Button saveButton;
    private final Button cancelButton;

    private Task editingTask;

    public TaskFormPane(TaskService taskService, Runnable onDone) {
        this.taskService = taskService;
        this.onDone = onDone;

        setSpacing(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #fcfcfc;");

        headerLabel = new Label("Add New Task");
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        titleField = new TextField();
        titleField.setPromptText("Title (e.g., Implement heap)");

        courseField = new TextField();
        courseField.setPromptText("Course (e.g., INFO 6205)");

        dueDateField = new TextField();
        dueDateField.setPromptText("Due date (YYYY-MM-DD)");

        minutesField = new TextField();
        minutesField.setPromptText("Estimated minutes (e.g., 60)");

        difficultyField = new TextField();
        difficultyField.setPromptText("Difficulty 1–5");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");

        Label prereqLabel = new Label("Prerequisites (optional)");
        prereqLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        prereqListView = new ListView<>();
        prereqListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        prereqListView.setPrefHeight(120);
        prereqListView.setCellFactory(listView -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        saveButton = new Button("Save Task");
        cancelButton = new Button("Cancel");

        HBox buttonRow = new HBox(8, saveButton, cancelButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        // Layout
        getChildren().addAll(
                headerLabel,
                labeled("Title", titleField),
                labeled("Course", courseField),
                labeled("Due date", dueDateField),
                labeled("Estimated minutes", minutesField),
                labeled("Difficulty", difficultyField),
                prereqLabel,
                prereqListView,
                errorLabel,
                buttonRow
        );

        VBox.setVgrow(this, Priority.ALWAYS);

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> {
            clearForm();
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    private VBox labeled(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        return new VBox(2, label, field);
    }

    // Public APIs for Main Layout

    /** Prepare the form for adding a brand new task. */
    public void startAddMode() {
        editingTask = null;
        headerLabel.setText("Add New Task");
        saveButton.setText("Save Task");

        clearForm();
        refreshPrerequisites();
    }

    /** Prepare the form for editing an existing task. */
    public void startEditMode(Task task) {
        if (task == null) {
            startAddMode();
            return;
        }

        editingTask = task;
        headerLabel.setText("Edit Task");
        saveButton.setText("Save Changes");

        // populate fields from existing task
        titleField.setText(task.getTitle());
        courseField.setText(task.getCourse());
        dueDateField.setText(task.getDueDate().toString());
        minutesField.setText(Integer.toString(task.getEstimatedMinutes()));
        difficultyField.setText(Integer.toString(task.getDifficulty()));

        errorLabel.setText("");

        refreshPrerequisites();

        // To pre-select prerequisites here via TaskService
        prereqListView.getSelectionModel().clearSelection();
    }

    /** Refresh prereq choices from current tasks. Call before showing form. */
    public void refreshPrerequisites() {
        List<Task> tasks = taskService.getAllTasksSorted();
        prereqListView.setItems(FXCollections.observableArrayList(tasks));
    }

    private void handleSave() {
        errorLabel.setText("");

        String title = titleField.getText().trim();
        String course = courseField.getText().trim();
        String dueDateText = dueDateField.getText().trim();
        String minutesText = minutesField.getText().trim();
        String difficultyText = difficultyField.getText().trim();

        if (title.isEmpty()) {
            errorLabel.setText("Title is required.");
            return;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateText);
        } catch (DateTimeParseException ex) {
            errorLabel.setText("Invalid date. Use format YYYY-MM-DD.");
            return;
        }

        int minutes;
        int difficulty;
        try {
            minutes = Integer.parseInt(minutesText);
            difficulty = Integer.parseInt(difficultyText);
        } catch (NumberFormatException ex) {
            errorLabel.setText("Minutes and difficulty must be numbers.");
            return;
        }

        if (difficulty < 1 || difficulty > 5) {
            errorLabel.setText("Difficulty must be between 1 and 5.");
            return;
        }

        if (editingTask == null) {
            // Create a new Task
            int id = taskService.getAllTasks().size() + 1; // simple ID

            Task task = new Task(id, title, course, dueDate, minutes, difficulty);
            taskService.addTask(task);

            // add prereq edges in the graph
            for (Task prereq : prereqListView.getSelectionModel().getSelectedItems()) {
                taskService.addDependency(prereq, task);
            }
        } else {
        	taskService.updateTask(editingTask, title, course, dueDate, minutes, difficulty);
        }

        clearForm();

        if (onDone != null) {
            onDone.run();  // MainLayout will refresh list + switch view
        }
    }

    private void clearForm() {
        titleField.clear();
        courseField.clear();
        dueDateField.clear();
        minutesField.clear();
        difficultyField.clear();
        errorLabel.setText("");
        prereqListView.getSelectionModel().clearSelection();
    }
}