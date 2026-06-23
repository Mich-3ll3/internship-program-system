package mx.uv.internshipprogramsystem.gui.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;

public class InternshipProgramSystem extends Application {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(InternshipProgramSystem.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/uv/internshipprogramsystem/gui/fxml/WindowManagerGUI.fxml"));
            BorderPane marcoPrincipal = loader.load();

            WindowManagerController.setMainLayout(marcoPrincipal);

            WindowManagerController.changeView("LoginDashboard.fxml");

            Scene scene = new Scene(marcoPrincipal);
            primaryStage.setTitle("Internship Program System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            LOGGER.error("Error al iniciar la aplicacion", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}