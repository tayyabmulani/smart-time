package smarttime.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TodayOverviewPane extends VBox {

    public TodayOverviewPane() {
        setSpacing(8);
        setPadding(new Insets(8));
        setStyle("-fx-background-color: #fcfcfc;");

        Label header = new Label("Today Overview");
        header.setStyle("-fx-font-weight: bold;");

        Label body = new Label(
                "Later this panel will show focus blocks, deadlines,\n" +
                "and the recommended next task from the heap.\n\n" +
                "For now this is a static placeholder."
        );
        body.setStyle("-fx-text-fill: #555555;");

        getChildren().addAll(header, body);
    }
}