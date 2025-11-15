package smarttime.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StatusBar extends HBox {

    private final Label statusLabel;

    public StatusBar() {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(6, 10, 6, 10));
        setStyle("-fx-background-color: #eeeeee;");

        statusLabel = new Label("Status: demo layout only – logic coming next.");
        getChildren().add(statusLabel);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }
}