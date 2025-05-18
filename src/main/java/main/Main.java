package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main/view/layout.fxml"));
        stage.setTitle("FXML JavaFX Example");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/main/view/style.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

