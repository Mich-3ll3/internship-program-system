package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class ProjectsMenuController {

    @FXML private Button btnRequestProject;
    @FXML private Button btnAssignProject;
    @FXML private Button btnDeleteProject;
    @FXML private Button btnValidateProject;
    @FXML private Button btnSearchResponsible;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onRequestProject() {
        showInfo("Solicitar Proyecto", "Abrir ventana para solicitar proyecto.");
    }

    @FXML
    private void onAssignProject() {
        showInfo("Asignar Proyecto", "Abrir ventana para asignar proyecto.");
    }

    @FXML
    private void onDeleteProject() {
        showInfo("Eliminar Proyecto", "Abrir ventana para eliminar proyecto.");
    }

    @FXML
    private void onValidateProject() {
        showInfo("Validar Proyecto", "Abrir ventana para validar proyecto.");
    }
    
    @FXML
    private void onSearchResponsible() {
        showInfo("Responsables del Proyecto", "Abrir ventana para buscar responsables.");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
