package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorReportsDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorReportsDashboardController.class);

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
    
    @FXML private TextField txtSearchReport;
    @FXML private ComboBox<String> cmbReportType;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private Button btnReviewReport;

    @FXML private TableView<?> tblReports; // TODO: Sustituir '?' por tu clase ReportDTO
    @FXML private TableColumn<?, String> colInternName;
    @FXML private TableColumn<?, String> colReportType;
    @FXML private TableColumn<?, Integer> colHours;
    @FXML private TableColumn<?, String> colDate;
    @FXML private TableColumn<?, String> colStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando el panel de revisión de reportes del profesor.");
        tblReports.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupFilters();
    }

    private void setupFilters() {
        cmbReportType.getItems().addAll("Todos", "Mensual", "Parcial", "Final");
        cmbReportType.getSelectionModel().selectFirst();
        
        cmbStatusFilter.getItems().addAll("Todos", "Pendiente", "Aprobado", "Rechazado");
        cmbStatusFilter.getSelectionModel().selectFirst();
    }

    // ==========================================
    // MÉTODOS ESPECÍFICOS DEL MÓDULO (INGLÉS)
    // ==========================================

    @FXML
    private void reviewSelectedReport(ActionEvent event) {
        LOGGER.info("Se ha solicitado evaluar el reporte seleccionado en la tabla.");
        showInfo(MSG_IN_DEVELOPMENT);
        // TODO: Lógica para evaluar el reporte
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
        LOGGER.info("Navegando al módulo de Alumnos (Interns).");
        WindowManagerController.changeView("ProfessorInternsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Documentos.");
        WindowManagerController.changeView("ProfessorDocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Recargando la vista del módulo de Reportes.");
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