package smarttime.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import smarttime.service.TaskService;

/**
 * Status bar shown at the bottom of the app.
 * Now includes an Undo button that uses our custom UndoStack via TaskService.
 */
public class StatusBar extends HBox {

    private final TaskService taskService;
    private final Runnable onAfterUndo;

    private final Label statusLabel;
    private final Button undoButton;

    public StatusBar(TaskService taskService, Runnable onAfterUndo) {
        this.taskService = taskService;
        this.onAfterUndo = onAfterUndo;

        setSpacing(12);
        setPadding(new Insets(6, 10, 6, 10));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dddddd; -fx-border-width: 1 0 0 0;");

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");

        undoButton = new Button("Undo");
        undoButton.setStyle("-fx-font-size: 11px;");
        undoButton.setOnAction(e -> handleUndo());

        getChildren().addAll(statusLabel, undoButton);

        updateUndoState();
    }

    private void handleUndo() {
        if (!taskService.canUndo()) {
            setStatusText("Nothing to undo.");
            updateUndoState();
            return;
        }

        taskService.undoLastAction();

        // Let the UI refresh the task list etc.
        if (onAfterUndo != null) {
            onAfterUndo.run();
        }

        setStatusText("Last action undone.");
        updateUndoState();
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    /**
     * Enable/disable Undo button based on whether there is anything to undo.
     */
    public void updateUndoState() {
        boolean canUndo = taskService != null && taskService.canUndo();
        undoButton.setDisable(!canUndo);
    }
}