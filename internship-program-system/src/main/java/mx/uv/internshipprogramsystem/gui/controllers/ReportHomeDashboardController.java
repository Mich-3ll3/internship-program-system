package mx.uv.internshipprogramsystem.gui.controllers;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.ReportManager;
import mx.uv.internshipprogramsystem.logic.managers.ReportExporterManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class ReportHomeDashboardController implements Initializable {

    @FXML private Button btnRegisterReport;
    @FXML private Button btnGenerateReport;
    @FXML private Button btnUploadPDF;
    @FXML private Button btnSendReport;
    @FXML private TextArea txtReportNotes;

    @FXML private TableView<ReportDTO> tblReports;
    @FXML private TableColumn<ReportDTO, Integer> colNumber;
    @FXML private TableColumn<ReportDTO, String> colType;
    @FXML private TableColumn<ReportDTO, LocalDate> colDate;
    @FXML private TableColumn<ReportDTO, String> colStatus;
    @FXML private TextField txtSearchNumber;

    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportManager reportManager = new ReportManager();
    private final ReportExporterManager exporterManager = new ReportExporterManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            List<ReportDTO> reports = reportDAO.getAllReports();
            tblReports.setItems(FXCollections.observableArrayList(reports));
        } catch (BusinessException e) {
            showError("Error cargando reportes: " + e.getMessage());
        }
    }

    @FXML
    private void openRegisterReport(ActionEvent event) {
        WindowManagerController.changeView("RegisterReport.fxml");
    }

    @FXML
    private void generateReportPDF(ActionEvent event) {
        ReportDTO selectedReport = tblReports.getSelectionModel().getSelectedItem();

        if (selectedReport == null) {
            showWarning("Debes seleccionar un reporte de la lista antes de generar el PDF.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte PDF como...");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            fileChooser.setInitialFileName("Reporte_Mensual_No_" + selectedReport.getNumber() + ".pdf");

            Stage stage = (Stage) tblReports.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {
                
                Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
                
                if (currentIntern.isPresent()) {
                    Optional<MonthlyReportContextDTO> contextOpt = reportManager.generateMonthlyContext(currentIntern.get().getId());

                    if (contextOpt.isPresent()) {
                        boolean success = exporterManager.generatePlainPdfReport(selectedReport, contextOpt.get(), selectedFile.getAbsolutePath());
                        
                        if (success) {
                            showSuccess("El reporte PDF se generó y guardó correctamente en:\n" + selectedFile.getAbsolutePath());
                        } else {
                            showError("Ocurrió un problema interno al escribir el archivo PDF.");
                        }
                    } else {
                        showError("No se pudo obtener el contexto de la base de datos para llenar el PDF.");
                    }
                } else {
                    showError("No hay un Intern activo en la sesión.");
                }
            }
        } catch (ValidationException e) {
            showError("Error de validación al generar el PDF: " + e.getMessage());
        } catch (Exception e) {
            showError("Ocurrió un problema inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void uploadReportFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar PDF para subir");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            Stage stage = (Stage) tblReports.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                String filePath = file.getAbsolutePath();
                ReportDTO selectedReport = tblReports.getSelectionModel().getSelectedItem();
                
                if (selectedReport != null) {
                    selectedReport.setFilePath(filePath);
                    reportDAO.updateReportFilePath(selectedReport.getNumber(), filePath);
                    showSuccess("La ruta del archivo PDF se guardó en la base de datos:\n" + filePath);
                } else {
                    showWarning("Debes seleccionar un reporte de la lista para asociar la ruta del archivo.");
                }
            } else {
                showInfo("No seleccionaste ningún archivo.");
            }
        } catch (Exception e) {
            showError("No se pudo guardar la ruta del archivo: " + e.getMessage());
        }
    }

    @FXML
    private void sendReport(ActionEvent event) {
        showInfo("Funcionalidad de envío pendiente de implementación.");
    }

    @FXML
    private void handleSearch() {
        try {
            int searchNumber = Integer.parseInt(txtSearchNumber.getText());
            List<ReportDTO> reports = reportDAO.getAllReports();
            List<ReportDTO> filtered = reports.stream()
                    .filter(r -> r.getNumber() == searchNumber)
                    .toList();
            tblReports.setItems(FXCollections.observableArrayList(filtered));

            if (filtered.isEmpty()) {
                showInfo("No se encontró ningún reporte con número " + searchNumber);
            }
        } catch (NumberFormatException e) {
            showError("El número de búsqueda debe ser un valor entero.");
        } catch (BusinessException e) {
            showError("Error buscando reportes: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        try {
            List<ReportDTO> reports = reportDAO.getAllReports();
            tblReports.setItems(FXCollections.observableArrayList(reports));
            txtSearchNumber.clear();
        } catch (BusinessException e) {
            showError("No se pudieron recargar los reportes: " + e.getMessage());
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        WindowManagerController.changeView("ProjectsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent event) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}