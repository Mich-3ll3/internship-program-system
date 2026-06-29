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

public class ProfessorDocumentsDashboardController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorDocumentsDashboardController.class);

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
    
    @FXML private TextField txtSearchDocument;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private Button btnReviewDocument;

    @FXML private TableView<?> tblDocuments;
    @FXML private TableColumn<?, String> colInternName;
    @FXML private TableColumn<?, String> colDocumentType;
    @FXML private TableColumn<?, String> colUploadDate;
    @FXML private TableColumn<?, String> colStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Inicializando el panel de revisión de documentos del profesor.");
        tblDocuments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupFilters();
    }

    private void setupFilters() {
        cmbStatusFilter.getItems().addAll(
            "Todos",
            "Pendiente de revisión",
            "Aprobado",
            "Con observaciones"
        );
        cmbStatusFilter.getSelectionModel().selectFirst();
    }

    // ==========================================
    // MÉTODOS ESPECÍFICOS DEL MÓDULO (INGLÉS)
    // ==========================================

    @FXML
    private void reviewSelectedDocument(ActionEvent event) {
        LOGGER.info("Se ha solicitado evaluar el documento seleccionado en la tabla.");
        showInfo(MSG_IN_DEVELOPMENT);
    }

    // ==========================================
    // MÉTODOS DE NAVEGACIÓN (INGLÉS)
    // ==========================================

    @FXML
    private void goHome(ActionEvent event) {
        LOGGER.info("Navegando de regreso al panel de inicio del profesor.");
        WindowManagerController.changeView("ProfessorHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        LOGGER.info("Cerrando la sesión del profesor.");
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
        LOGGER.info("Recargando la vista del módulo de Documentos.");
        WindowManagerController.changeView("ProfessorDocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        LOGGER.info("Navegando al módulo de Reportes.");
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