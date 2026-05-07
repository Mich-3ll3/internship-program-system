package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowManagerController {

    private static BorderPane mainLayout;
    private static final Logger LOGGER = Logger.getLogger(WindowManagerController.class.getName());

    @FXML
    private StackPane containerArea;

    public static void setMainLayout(BorderPane layout) {
        mainLayout = layout;
    }

    public static void changeView(String fxmlName) {
        Platform.runLater(() -> {
            try {
                URL fxmlUrl = WindowManagerController.class.getResource("/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName);
                if (fxmlUrl == null) {
                    throw new IOException("No se encontró el archivo FXML en: /fxml/" + fxmlName);
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent view = loader.load();

                if (mainLayout != null) {
                    mainLayout.setCenter(view);
                } else {
                    LOGGER.log(Level.SEVERE, "Error: mainLayout es NULL. Llama a setMainLayout primero.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Fallo crítico al cargar la vista: " + fxmlName, e);
            }
        });
    }
}
