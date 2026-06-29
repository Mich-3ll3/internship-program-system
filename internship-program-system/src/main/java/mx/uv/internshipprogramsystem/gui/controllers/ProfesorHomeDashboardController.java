package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ProfesorHomeDashboardController {

    @FXML
    private javafx.scene.control.Label lblProfessorName;

    @FXML
    private javafx.scene.control.Label lblProfessorDetails;

    @FXML
    private void initialize() {
        initializeProfile();
    }

    private void initializeProfile() {
        mx.uv.internshipprogramsystem.logic.dto.UserDTO currentUser =
            mx.uv.internshipprogramsystem.logic.managers.UserSessionManager.getCurrentUser();
        if (currentUser != null) {
            lblProfessorName.setText(currentUser.getFullName());
            String details = "Ingeniería en Software • Profesor • "
                + currentUser.getInstitutionalEmail();
            lblProfessorDetails.setText(details);
        }
    }

    @FXML
    private void abrirAlumnos(ActionEvent event) {
        WindowManagerController.changeView("ProfessorStudentDashboard.fxml");
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
        WindowManagerController.changeView("ProfessorReportDashboard.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void evaluarAlumnoSeleccionado(ActionEvent event) {
        showInfo("Función 'Evaluar alumno' pendiente de implementación.");
    }

    @FXML
    private void registrarEvaluacion(ActionEvent event) {
        showInfo("Función 'Registrar evaluación' pendiente de implementación.");
    }

    @FXML
    private void abrirReporteSeleccionado(ActionEvent event) {
        showInfo("Función 'Abrir reporte' pendiente de implementación.");
    }

    @FXML
    private void enviarRecordatorioReporte(ActionEvent event) {
        showInfo("Función 'Enviar recordatorio' pendiente de implementación.");
    }

    @FXML
    private void aprobarReporte(ActionEvent event) {
        showInfo("Función 'Aprobar reporte' pendiente de implementación.");
    }

    @FXML
    private void pedirCambiosReporte(ActionEvent event) {
        showInfo("Función 'Pedir cambios' pendiente de implementación.");
    }

    @FXML
    private void abrirDocumentos(ActionEvent event) {
        WindowManagerController.changeView("ProfessorDocumentDashboard.fxml");
    }
    
    @FXML
    private void guardarEstadoDocumento(ActionEvent event) {
        showInfo("Función 'Guardar estado del documento' pendiente de implementación.");
    }
    
    @FXML
    private void subirDocumento(ActionEvent event) {
        showInfo("Función 'Subir documento' pendiente de implementación.");
    }

    private void showInfo(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
