package smarttime;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import smarttime.ui.MainLayout;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainLayout root = new MainLayout();    
        Scene scene = new Scene(root, 900, 550);
        stage.setTitle("SmartTime");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}