package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorInternsDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorInternsDashboardController.class);

    private static final String MSG_IN_DEVELOPMENT = "Esta funcionalidad estará disponible en la próxima versión.";

    // ==========================================
    // COMPONENTES DE LA INTERFAZ (FXML)
    // ==========================================
    @FXML private Button btnHome;
    @FXML private Button btnInternsNav;
    @FXML private Button btnDocumentosNav;
    @FXML private Button btnReportesNav;
    @FXML private Button btnSalir;
    @FXML private Button btnBack;
    
    @FXML private TextField txtSearchIntern;
    @FXML private Button btnViewDetails;

    @FXML private TableView<?> tblInterns;
    @FXML private TableColumn<?, String> colEnrollment;
    @FXML private TableColumn<?, String> colName;
    @FXML private TableColumn<?, String> colProject;
    @FXML private TableColumn<?, String> colProgress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando el panel de gestión de alumnos (Interns Dashboard).");
        tblInterns.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ==========================================
    // MÉTODOS ESPECÍFICOS DEL MÓDULO (INGLÉS)
    // ==========================================

    @FXML
    private void viewInternDetails(ActionEvent event) {
        LOGGER.info("Solicitando la apertura del expediente detallado del alumno.");
        showInfo(MSG_IN_DEVELOPMENT);
    }
    
    // ==========================================
    // MÉTODOS DE NAVEGACIÓN (INGLÉS)
    // ==========================================

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Navegando al panel de inicio del profesor.");
        WindowManagerController.changeView("ProfessorHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        LOGGER.info("Cerrando la sesión de trabajo del profesor.");
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void goInternsModule(ActionEvent event) {
        LOGGER.info("Recargando el módulo actual de Alumnos (Interns).");
        WindowManagerController.changeView("ProfessorInternsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Documentos.");
        WindowManagerController.changeView("ProfessorDocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Reportes del profesor.");
        WindowManagerController.changeView("ProfessorReportsDashboard.fxml");
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