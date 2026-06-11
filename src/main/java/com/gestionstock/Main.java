package com.gestionstock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gestionstock/main.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Gestion Stock IAGE");
        stage.setScene(scene);
        stage.show();
    }
}