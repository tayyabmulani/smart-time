package smarttime.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
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

    private final DatePicker dueDatePicker;
    private final Slider minutesSlider;
    private final Label minutesValueLabel;
    private final Slider difficultySlider;
    private final Label difficultyValueLabel;

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

        // Due date as DatePicker
        dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("YYYY-MM-DD");

        // Estimated minutes slider
        minutesSlider = new Slider(15, 120, 60);   // min, max, initial

        // visual behaviour
        minutesSlider.setShowTickMarks(true);
        minutesSlider.setShowTickLabels(false);    // <— hide crowded numbers
        minutesSlider.setMajorTickUnit(15);
        minutesSlider.setMinorTickCount(2);
        minutesSlider.setBlockIncrement(5);
        minutesSlider.setSnapToTicks(true);

        // label on the right: "60 min"
        minutesValueLabel = new Label("60 min");
        minutesValueLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        // update text when user drags the slider
        minutesSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int rounded = (int) Math.round(newVal.doubleValue());
            minutesValueLabel.setText(rounded + " min");
        });

        // lay them out nicely in one row
        HBox minutesRow = new HBox(8, minutesSlider, minutesValueLabel);
        minutesRow.setAlignment(Pos.CENTER_LEFT);
        minutesRow.setFillHeight(false);
        minutesRow.setPrefWidth(260);

        // --- Difficulty slider 1–5 ---
        difficultySlider = new Slider();
        difficultySlider.setMin(1);
        difficultySlider.setMax(5);
        difficultySlider.setBlockIncrement(1);
        difficultySlider.setMajorTickUnit(1);
        difficultySlider.setMinorTickCount(0);
        difficultySlider.setShowTickMarks(true);
        difficultySlider.setShowTickLabels(true);
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setValue(3); // default

        difficultyValueLabel = new Label("3");
        difficultyValueLabel.setStyle("-fx-font-size: 11px;");
        difficultySlider.valueProperty().addListener((obs, oldV, newV) -> {
            int val = (int) Math.round(newV.doubleValue());
            difficultySlider.setValue(val);
            difficultyValueLabel.setText(Integer.toString(val));
        });

        HBox difficultyRow = new HBox(8, difficultySlider, difficultyValueLabel);
        difficultyRow.setAlignment(Pos.CENTER_LEFT);

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
        	    labeled("Title", titleField, true),
        	    labeled("Course", courseField, true),
        	    labeled("Due date", dueDatePicker, true),
        	    labeled("Estimated minutes", minutesRow, false),
        	    labeled("Difficulty", difficultyRow, true),
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

    // Generic "label + control" helper
    private VBox labeled(String labelText, Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        return new VBox(2, label, field);
    }

    // Public APIs for MainLayout

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

        titleField.setText(task.getTitle());
        courseField.setText(task.getCourse());
        dueDatePicker.setValue(task.getDueDate());
        minutesSlider.setValue(task.getEstimatedMinutes());
        minutesValueLabel.setText(task.getEstimatedMinutes() + " min");
        difficultySlider.setValue(task.getDifficulty());
        difficultyValueLabel.setText(Integer.toString(task.getDifficulty()));

        errorLabel.setText("");

        refreshPrerequisites();
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
        LocalDate dueDate = dueDatePicker.getValue();

        if (title.isEmpty()) {
            errorLabel.setText("Title is required.");
            return;
        }

        if (dueDate == null) {
            errorLabel.setText("Please select a due date.");
            return;
        }

        int minutes = (int) Math.round(minutesSlider.getValue());
        int difficulty = (int) Math.round(difficultySlider.getValue());

        if (difficulty < 1 || difficulty > 5) {
            errorLabel.setText("Difficulty must be between 1 and 5.");
            return;
        }

        try {
            // The LocalDate from DatePicker is already parsed, so no DateTimeParseException
            // This try block is mostly here to mirror your previous validation style.
            dueDate.toString();
        } catch (DateTimeParseException ex) {
            errorLabel.setText("Invalid date.");
            return;
        }

        if (editingTask == null) {
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
            onDone.run(); // MainLayout will refresh list + switch view
        }
    }

    private void clearForm() {
        titleField.clear();
        courseField.clear();
        dueDatePicker.setValue(null);
        minutesSlider.setValue(60);
        minutesValueLabel.setText("60 min");
        difficultySlider.setValue(3);
        difficultyValueLabel.setText("3");
        errorLabel.setText("");
        prereqListView.getSelectionModel().clearSelection();
    }
    private VBox labeled(String labelText, Node field, boolean required) {
        String text = required ? labelText + " *" : labelText;
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        return new VBox(2, label, field);
    }
}