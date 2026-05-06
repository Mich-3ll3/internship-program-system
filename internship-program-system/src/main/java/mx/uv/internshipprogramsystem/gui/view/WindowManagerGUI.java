package mx.uv.internshipprogramsystem.gui.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.internshipprogramsystem.gui.controller.WindowManagerController;

public class WindowManagerGUI extends Application {
    private static final Logger LOGGER = Logger.getLogger(WindowManagerGUI.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/WindowManagerGUI.fxml"));
            BorderPane root = loader.load();
            
            WindowManagerController.setMainLayout(root);
            WindowManagerController.changeView("LoginGUI.fxml");

            Scene scene = new Scene(root);
            primaryStage.setTitle("Sistema de Gestión de Prácticas");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}