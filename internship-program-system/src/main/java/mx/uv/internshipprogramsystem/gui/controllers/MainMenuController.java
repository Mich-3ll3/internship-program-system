package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class MainMenuController {

    @FXML private Button btnStudents;
    @FXML private Button btnTeachers;
    @FXML private Button btnOrganizations;
    @FXML private Button btnProjects;
    @FXML private Button btnResponsibles;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnEvaluations;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onStudents() {
        showInfo("Alumnos", "Abrir ventana de gestión de alumnos.");
    }

    @FXML
    private void onTeachers() {
        showInfo("Profesores", "Abrir ventana de gestión de profesores.");
    }

    @FXML
    private void onOrganizations() {
        showInfo("Organizaciones", "Abrir ventana de gestión de organizaciones.");
    }

    @FXML
    private void onProjects() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjectsMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Submenú de Proyectos - SPP");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onResponsibles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResponsiblesMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Submenú de Responsibles - SPP");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("No se pudo abrir el submenú de responsables.");
        }
    }

    @FXML
    private void onDocuments() {
        showInfo("Documentos", "Abrir ventana de gestión de documentos.");
    }

    @FXML
    private void onReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Report.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registrar Reporte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEvaluations() {
        showInfo("Evaluaciones", "Abrir ventana de gestión de evaluaciones.");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
