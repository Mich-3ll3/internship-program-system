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
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
    }

    @FXML
    private void evaluarAlumnoSeleccionado(ActionEvent event) {
    }

    @FXML
    private void registrarEvaluacion(ActionEvent event) {
    }

    @FXML
    private void abrirReporteSeleccionado(ActionEvent event) {
    }

    @FXML
    private void enviarRecordatorioReporte(ActionEvent event) {
    }

    @FXML
    private void aprobarReporte(ActionEvent event) {
    }

    @FXML
    private void pedirCambiosReporte(ActionEvent event) {
    }

    @FXML
    private void abrirDocumentos(ActionEvent event) {
    }
    
    @FXML
    private void guardarEstadoDocumento(ActionEvent event) {
    }
    
    @FXML
    private void subirDocumento(ActionEvent event) {
    }

}
