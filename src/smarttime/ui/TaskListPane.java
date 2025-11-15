package smarttime.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TaskListPane extends VBox {

    private final ListView<String> taskList;
    private final Button addTaskButton;

    public TaskListPane() {
        setSpacing(8);
        setPadding(new Insets(8));
        setPrefWidth(280);
        setStyle("-fx-background-color: #ffffff;");

        Label header = new Label("Tasks");
        header.setStyle("-fx-font-weight: bold;");

        taskList = new ListView<>();
        taskList.getItems().addAll(
                "Read INFO 6205 notes",
                "Implement heap for SmartTime",
                "Review recursion problems"
        );
        VBox.setVgrow(taskList, Priority.ALWAYS);

        addTaskButton = new Button("+ Add Task (placeholder)");
        addTaskButton.setMaxWidth(Double.MAX_VALUE);

        getChildren().addAll(header, taskList, addTaskButton);
    }
}