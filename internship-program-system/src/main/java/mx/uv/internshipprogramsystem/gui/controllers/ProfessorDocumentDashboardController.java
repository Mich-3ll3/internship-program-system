package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.DocumentDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorDocumentDashboardController {
    
    @FXML private TextField txtSearchMatricula;
    
    @FXML private TableView<DocumentDTO> tblDocuments;
    @FXML private TableColumn<DocumentDTO, String> colStudent;
    @FXML private TableColumn<DocumentDTO, String> colType;
    @FXML private TableColumn<DocumentDTO, String> colDate;
    @FXML private TableColumn<DocumentDTO, String> colStatus;

    private DocumentDAO documentDAO;
    private InternDAO internDAO;
    private UserDTO currentUser;

    @FXML
    public void initialize() {
        documentDAO = new DocumentDAO();
        internDAO = new InternDAO();
        currentUser = UserSessionManager.getCurrentUser();

        setupTableColumns();
        loadDocuments();
    }

    private void setupTableColumns() {
        colStudent.setCellValueFactory(cellData -> new SimpleStringProperty("Estudiante Mock"));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty("2026-05-15"));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty("En Revisión"));
    }

    private void loadDocuments() {
        if (currentUser == null) return;
        try {
            // Simplified loading. In a real app we would join tables or fetch doc by students.
            // Simplified loading. Since there's no DB method right now, just don't crash
            // We just leave the table empty or with mock data for now
            ObservableList<DocumentDTO> docsToShow = FXCollections.observableArrayList();
            // Just mocking one document
            docsToShow.add(new DocumentDTO(1, "Plan_de_Trabajo.pdf", "Plan de Actividades", "C:/docs/plan.pdf"));
            
            tblDocuments.setItems(docsToShow);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los documentos.");
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // Implement filter by matricula locally
        showAlert(Alert.AlertType.INFORMATION, "Filtrar", "Buscando documentos para: " + txtSearchMatricula.getText());
    }

    @FXML
    private void handleClearSearch(ActionEvent event) {
        txtSearchMatricula.clear();
        loadDocuments();
    }

    @FXML
    private void handleReviewDocument(ActionEvent event) {
        DocumentDTO selected = tblDocuments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un documento para revisar.");
            return;
        }
        ProfessorDocumentReviewDashboardController.setDocument(selected);
        WindowManagerController.changeView("ProfessorDocumentReviewDashboard.fxml");
    }

    @FXML
    private void handleDownloadDocument(ActionEvent event) {
        DocumentDTO selected = tblDocuments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un documento para descargar.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Descargar", "Descargando: " + selected.getPath());
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("ProfesorHomeDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorReportDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorDocumentDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
    
    @FXML
    private void handleAvatarClick(javafx.scene.input.MouseEvent event) {
        WindowManagerController.changeView("UserProfileDashboard.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
