package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dao.ReportDeadlineDAO;
import mx.uv.internshipprogramsystem.logic.dao.NotificationDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDeadlineDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ProfessorReportDashboardController {
    
    @FXML private TextField txtReportNumber;
    @FXML private DatePicker dpDeadline;
    
    @FXML private TableView<ReportDTO> tblReports;
    @FXML private TableColumn<ReportDTO, String> colStudent;
    @FXML private TableColumn<ReportDTO, String> colNumber;
    @FXML private TableColumn<ReportDTO, String> colDate;
    @FXML private TableColumn<ReportDTO, String> colStatus;
    @FXML private TableColumn<ReportDTO, String> colAction;

    private ReportDAO reportDAO;
    private ReportDeadlineDAO reportDeadlineDAO;
    private InternDAO internDAO;
    private NotificationDAO notificationDAO;
    private UserDTO currentUser;

    @FXML
    public void initialize() {
        reportDAO = new ReportDAO();
        reportDeadlineDAO = new ReportDeadlineDAO();
        internDAO = new InternDAO();
        notificationDAO = new NotificationDAO();
        currentUser = UserSessionManager.getCurrentUser();

        setupTableColumns();
        loadReports();
    }

    private void setupTableColumns() {
        colStudent.setCellValueFactory(cellData -> {
            try {
                // Not ideal for performance, but good for prototype:
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getStudentId()));
            } catch (Exception e) {
                return new SimpleStringProperty("Desconocido");
            }
        });
        colNumber.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colAction.setCellValueFactory(cellData -> new SimpleStringProperty("Ver Detalles"));
    }

    private void loadReports() {
        if (currentUser == null) return;
        try {
            // First get interns assigned to this professor
            List<InternDTO> interns = internDAO.findByProfessor(currentUser.getId());
            
            // Get all reports for the professor
            List<ReportDTO> reports = reportDAO.getReportsByProfessor(currentUser.getId());
            
            // Further filter (though getReportsByProfessor already does this)
            ObservableList<ReportDTO> observableReports = FXCollections.observableArrayList(reports);
            tblReports.setItems(observableReports);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los reportes.");
        }
    }

    @FXML
    private void handleSaveDeadline(ActionEvent event) {
        String numStr = txtReportNumber.getText();
        LocalDate date = dpDeadline.getValue();
        
        if (numStr == null || numStr.trim().isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor ingresa número de reporte y selecciona una fecha.");
            return;
        }

        try {
            int num = Integer.parseInt(numStr);
            ReportDeadlineDTO deadline = new ReportDeadlineDTO(currentUser.getId(), num, date);
            reportDeadlineDAO.setDeadline(deadline);
            
            // Notify assigned interns
            List<InternDTO> interns = internDAO.findByProfessor(currentUser.getId());
            for (InternDTO intern : interns) {
                notificationDAO.addNotification(intern.getId(), "La fecha límite para el reporte " + num + " es el " + date.toString());
            }

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Fecha límite guardada y estudiantes notificados.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Número de reporte inválido.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar la fecha.");
        }
    }

    @FXML
    private void handleReviewReport(ActionEvent event) {
        ReportDTO selected = tblReports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un reporte para evaluar.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Evaluar", "Función pendiente de implementación completa (Ventana de evaluación).");
    }

    @FXML
    private void handleDownloadReport(ActionEvent event) {
        ReportDTO selected = tblReports.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un reporte para descargar.");
            return;
        }
        showAlert(Alert.AlertType.INFORMATION, "Descargar", "Descargando: " + selected.getFilePath());
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
