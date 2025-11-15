package smarttime.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainLayout extends BorderPane {

    private final TaskListPane taskListPane;
    private final TodayOverviewPane todayOverviewPane;
    private final StatusBar statusBar;

    public MainLayout() {
        // ----- create children -----
        taskListPane = new TaskListPane();
        todayOverviewPane = new TodayOverviewPane();
        statusBar = new StatusBar();

        // ----- top: header -----
        VBox header = new VBox(4);
        Label title = new Label("SmartTime – Study & Time Planner");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label subtitle = new Label("Demo layout. Data structures will be wired later.");
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
    }
}