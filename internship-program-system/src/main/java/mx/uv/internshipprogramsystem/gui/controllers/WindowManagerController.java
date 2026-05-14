package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;

public class WindowManagerController {

    private static BorderPane mainLayout;
    private static final Logger LOGGER = Logger.getLogger(WindowManagerController.class.getName());
    private static final Deque<String> viewHistory = new ArrayDeque<>();
    private static String currentView;
    
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
                    if (currentView != null) {
                        viewHistory.push(currentView);
                    }
                    currentView = fxmlName;

                    mainLayout.setCenter(view);
                } else {
                    LOGGER.log(Level.SEVERE, "Error: mainLayout es NULL. Llama a setMainLayout primero.");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Fallo crítico al cargar la vista: " + fxmlName, e);
            }
        });
    }
    
    public static void changeViewToUpdateProfessor(String fxmlName, ProfessorDTO professor) {
        Platform.runLater(() -> {
            try {
                String path = "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName;
                URL fxmlUrl = WindowManagerController.class.getResource(path);
                
                if (fxmlUrl == null) {
                    throw new IOException("No se encontró el archivo FXML en: /fxml/" + fxmlName);
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent view = loader.load();

                Object controller = loader.getController();
                if (controller instanceof UpdateProfessorDashboardController updateController) {
                    updateController.setProfessorData(professor);
                }

                if (mainLayout != null) {
                    if (currentView != null) viewHistory.push(currentView);
                    currentView = fxmlName;
                    mainLayout.setCenter(view);
                }
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "Error al cargar vista de edición", exception);
            }
        });
    }
    
    public static void changeViewToUpdateIntern(String fxmlName, InternDTO intern) {
        Platform.runLater(() -> {
            try {
                String path = "/mx/uv/internshipprogramsystem/gui/fxml/" + fxmlName;
                URL fxmlUrl = WindowManagerController.class.getResource(path);
                
                if (fxmlUrl == null) {
                    throw new IOException("No se encontró el archivo FXML en: /fxml/" + fxmlName);
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent view = loader.load();

                Object controller = loader.getController();
                if (controller instanceof UpdateInternDashboardController updateController) {
                    updateController.setInternData(intern);
                }

                if (mainLayout != null) {
                    if (currentView != null) viewHistory.push(currentView);
                    currentView = fxmlName;
                    mainLayout.setCenter(view);
                }
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "Error al cargar vista de edición", exception);
            }
        });
    }
    
    public static void goBack() {
        Platform.runLater(() -> {
            if (!viewHistory.isEmpty()) {
                String previousView = viewHistory.pop();
                changeView(previousView);
            } else {
                LOGGER.log(Level.INFO, "No hay vista anterior en el historial.");
            }
        });
    }
}
