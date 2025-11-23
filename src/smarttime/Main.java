package smarttime;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import smarttime.ds.TaskGraph;
import smarttime.ds.TaskMinHeap;
import smarttime.service.TaskService;
import smarttime.ui.MainLayout;
import smarttime.util.SampleDataLoader;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // 1) Create core data structures
        TaskMinHeap heap = new TaskMinHeap(100); // initial capacity
        TaskGraph graph = new TaskGraph();

        // 2) Create the TaskService (central entry point for tasks)
        TaskService taskService = new TaskService(heap, graph);

        // 3) Load tasks + prerequisites from tasks.txt in smarttime/util
        SampleDataLoader.loadFromResource(taskService, "/smarttime/util/tasks.txt");

        // 4) Pass TaskService into MainLayout
        MainLayout root = new MainLayout(taskService);

        Scene scene = new Scene(root, 900, 550);
        stage.setTitle("SmartTime");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}