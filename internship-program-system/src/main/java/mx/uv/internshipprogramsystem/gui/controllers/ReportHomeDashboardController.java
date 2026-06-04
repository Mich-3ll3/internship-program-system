package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
    private void openRegisterReport(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("RegisterReport.fxml");
    }

    @FXML
    private void generateReportPDF(javafx.event.ActionEvent event) {
        ReportDTO selectedReport = tblReports.getSelectionModel().getSelectedItem();

        if (selectedReport == null) {
            showError("Debes seleccionar un reporte de la lista antes de generar el PDF.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte como PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            Stage stage = (Stage) tblReports.getScene().getWindow();
            java.io.File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                createPDF(selectedReport, file);
                showSuccess("El reporte " + selectedReport.getNumber() + " se guardó correctamente en:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Ocurrió un problema al generar el PDF: " + e.getMessage());
        }
    }

    private void createPDF(ReportDTO report, java.io.File file) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Reporte");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Número: " + report.getNumber());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Tipo: " + report.getType());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Fecha: " + report.getDate());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Estado: " + report.getStatus());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Observaciones: " + report.getGeneralObservations());
            contentStream.endText();

            contentStream.close();
            document.save(file);
        }
    }

    @FXML
    private void uploadReportFile(javafx.event.ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar PDF para subir");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            Stage stage = (Stage) tblReports.getScene().getWindow();
            java.io.File file = fileChooser.showOpenDialog(stage);

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
    private void sendReport(javafx.event.ActionEvent event) {
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
    private void goHome(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("ProjectsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }
    

    
    @FXML
    private void goSelfAssessmentsModule(javafx.event.ActionEvent event) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }
    
    

    @FXML
    private void logOut(javafx.event.ActionEvent event) {
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
