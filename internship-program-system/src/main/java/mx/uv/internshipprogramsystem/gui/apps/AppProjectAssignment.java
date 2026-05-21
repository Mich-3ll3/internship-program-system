package mx.uv.internshipprogramsystem.gui.apps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class AppProjectAssignment extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/projectAssignment.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Asignación de Proyecto");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
