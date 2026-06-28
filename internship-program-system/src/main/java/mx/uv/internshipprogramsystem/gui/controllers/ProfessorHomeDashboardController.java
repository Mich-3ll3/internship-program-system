package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorHomeDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorHomeDashboardController.class);

    // Mensajes dirigidos al usuario final
    private static final String MSG_IN_DEVELOPMENT = "Esta funcionalidad estará disponible en la próxima versión.";

    // Constantes de rastreo técnico internas en español
    private static final String LOG_HOME_NAVIGATION = "Intento de navegación a Inicio finalizado.";
    private static final String LOG_LOGOUT_FINISHED = "Intento de cierre de sesión finalizado.";
    private static final String LOG_STUDENTS_NAVIGATION = "Intento de navegación al módulo de Alumnos finalizado.";
    private static final String LOG_REPORTS_NAVIGATION = "Intento de navegación al módulo de Reportes finalizado.";
    private static final String LOG_DOCUMENTS_NAVIGATION = "Intento de navegación al módulo de Documentos finalizado.";

    // ==========================================
    // COMPONENTES DE LA INTERFAZ (FXML)
    // ==========================================
    @FXML private Button btnHome;
    @FXML private Button btnAlumnosNav;
    @FXML private Button btnDocumentosNav;
    @FXML private Button btnReportesNav;
    @FXML private Button btnSalir;

    @FXML private Label lblFecha;
    @FXML private Label lblProfessorInitials;
    @FXML private Label lblProfessorName;
    @FXML private Label lblProfessorDetails;
    
    @FXML private Label lblTotalAlumnos;
    @FXML private Label lblTotalGrupos;
    @FXML private Label lblAvanceProyecto;
    @FXML private ProgressBar progressProyecto;

    @FXML private Label lblPorRevisar;
    @FXML private Label lblReportesRecibidos;
    @FXML private Label lblAprobados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando el panel principal del profesor.");
    }

    // ==========================================
    // MÉTODOS DE NAVEGACIÓN EN INGLÉS
    // ==========================================

    @FXML
    private void goHome(ActionEvent event) {
        try {
            LOGGER.info("Recargando la vista del panel principal del profesor.");
            WindowManagerController.changeView("ProfessorHomeDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al navegar al módulo de Inicio: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug(LOG_HOME_NAVIGATION);
        }
    }

    @FXML
    private void logOut(ActionEvent event) {
        try {
            LOGGER.info("Cerrando la sesión del profesor.");
            UserSessionManager.clearSession();
            WindowManagerController.changeView("LoginDashboard.fxml");
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Error al cerrar la sesión: {}", runtimeException.getMessage());
        } finally {
            LOGGER.debug(LOG_LOGOUT_FINISHED);
        }
    }

    @FXML
    private void goStudentsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Alumnos.");
            showInfo(MSG_IN_DEVELOPMENT);
        } finally {
            LOGGER.debug(LOG_STUDENTS_NAVIGATION);
        }
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Reportes.");
            showInfo(MSG_IN_DEVELOPMENT);
        } finally {
            LOGGER.debug(LOG_REPORTS_NAVIGATION);
        }
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        try {
            LOGGER.info("Navegando al módulo de Documentos.");
            showInfo(MSG_IN_DEVELOPMENT);
        } finally {
            LOGGER.debug(LOG_DOCUMENTS_NAVIGATION);
        }
    }

    // ==========================================
    // ALERTAS
    // ==========================================

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}