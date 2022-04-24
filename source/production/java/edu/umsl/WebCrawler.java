package edu.umsl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WebCrawler extends Application {
    public void start(Stage primaryStage) throws Exception {
        Button button = new Button("OK");
        Scene scene = new Scene(button, 300, 250);
        primaryStage.setTitle("WebCrawler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
