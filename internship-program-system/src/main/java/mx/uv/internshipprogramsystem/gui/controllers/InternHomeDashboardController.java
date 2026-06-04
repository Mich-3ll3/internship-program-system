package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternHomeDashboardController
        implements Initializable {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            InternHomeDashboardController.class
        );

    @FXML
    private Button btnHome;

    @FXML
    private Button btnProjects;

    @FXML
    private Button btnDocuments;

    @FXML
    private Button btnReports;

    @FXML
    private Button btnExit;

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info(
            "Acceso al inicio del estudiante."
        );

        WindowManagerController.changeView(
            "InternHomeDashboard.fxml"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
        );
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de documentos."
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de reportes."
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesión realizado correctamente.");
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        LOGGER.info(
            "Dashboard de estudiante cargado correctamente."
        );
    }

    @FXML
    private void abrirProyecto(ActionEvent event) {
        LOGGER.info(
            "Apertura de detalle de proyecto."
        );
    }

    @FXML
    private void abrirDocumentos(ActionEvent event) {
        LOGGER.info(
            "Acceso a documentos del estudiante."
        );
    }

    @FXML
    private void subirDocumento(ActionEvent event) {
        LOGGER.info(
            "Carga de documento iniciada."
        );
    }

    @FXML
    private void verComentariosDocumento(
            ActionEvent event
    ) {
        LOGGER.info(
            "Consulta de comentarios del documento."
        );
    }

    @FXML
    private void abrirReportes(ActionEvent event) {
        LOGGER.info(
            "Consulta de historial de reportes."
        );
    }

    @FXML
    private void abrirReporte(ActionEvent event) {
        LOGGER.info(
            "Apertura de reporte individual."
        );
    }

    @FXML
    private void generarReporte(ActionEvent event) {
        LOGGER.info(
            "Generación de reporte iniciada."
        );
    }

    @FXML
    private void enviarReporte(ActionEvent event) {
        LOGGER.info(
            "Envío de reporte realizado."
        );
    }
}