package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.SelfAssessmentManager;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class SelfAssessmentHomeDashboardController implements Initializable {

    @FXML private Button btnRegisterSelfAssessment;
    @FXML private Button btnPrintSelfAssessment;
    @FXML private Button btnConsultSelfAssessment;

    @FXML private TableView<SelfAssessmentDTO> tblSelfAssessments;

    @FXML private TableColumn<SelfAssessmentDTO, String> colStudent;
    @FXML private TableColumn<SelfAssessmentDTO, String> colProject;
    @FXML private TableColumn<SelfAssessmentDTO, String> colResponsible;
    @FXML private TableColumn<SelfAssessmentDTO, String> colOrganization;
    @FXML private TableColumn<SelfAssessmentDTO, LocalDate> colDate;
    @FXML private TableColumn<SelfAssessmentDTO, String> colDepartment;
    @FXML private TableColumn<SelfAssessmentDTO, String> colPlace;

    @FXML private TextField txtSearchName;

    private final SelfAssessmentManager selfAssessmentManager = new SelfAssessmentManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colProject.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        colResponsible.setCellValueFactory(new PropertyValueFactory<>("responsibleName"));
        colOrganization.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colPlace.setCellValueFactory(new PropertyValueFactory<>("place"));

        loadAssessments();
    }

    private void loadAssessments() {
        try {
            List<SelfAssessmentDTO> assessments = selfAssessmentManager.getAllSelfAssessments();
            tblSelfAssessments.setItems(FXCollections.observableArrayList(assessments));
        } catch (BusinessException exception) {
            showError("Error cargando autoevaluaciones: " + exception.getMessage());
        }
    }

    @FXML
    private void openRegisterSelfAssessment(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("RegisterSelfAssessment.fxml");
    }

    @FXML
    private void printSelfAssessment(javafx.event.ActionEvent actionEvent) {
        SelfAssessmentDTO selected = tblSelfAssessments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Debes seleccionar una autoevaluación para imprimir.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar autoevaluación como PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

            Stage stage = (Stage) tblSelfAssessments.getScene().getWindow();
            java.io.File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                createPDF(selected, file);
                showSuccess("La autoevaluación del proyecto " + selected.getProjectName() +
                            " se guardó correctamente en:\n" + file.getAbsolutePath());
            }
        } catch (Exception exception) {
            showError("Ocurrió un problema al generar el PDF: " + exception.getMessage());
        }
    }

    private void createPDF(SelfAssessmentDTO assessment, java.io.File file) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Autoevaluación del Alumno");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Fecha: " + assessment.getDate());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Proyecto: " + assessment.getProjectName());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Departamento: " + assessment.getDepartment());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Lugar: " + assessment.getPlace());
            contentStream.endText();

            contentStream.close();
            document.save(file);
        }
    }

    @FXML
    private void consultSelfAssessment(javafx.event.ActionEvent actionEvent) {
        SelfAssessmentDTO selected = tblSelfAssessments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Debes seleccionar una autoevaluación para consultar.");
            return;
        }
        showInfo("Consulta de autoevaluación del proyecto: " + selected.getProjectName());
    }

    @FXML
    private void handleSearchByName(javafx.event.ActionEvent actionEvent) {
        String searchName = txtSearchName.getText().trim().toLowerCase();
        if (searchName.isEmpty()) {
            showWarning("Ingresa un nombre de proyecto para buscar.");
            return;
        }

        try {
            List<SelfAssessmentDTO> assessments = selfAssessmentManager.getAllSelfAssessments();
            List<SelfAssessmentDTO> filtered = new ArrayList<>();

            for (SelfAssessmentDTO assessment : assessments) {
                if (assessment.getProjectName() != null &&
                    assessment.getProjectName().toLowerCase().contains(searchName)) {
                    filtered.add(assessment);
                }
            }

            tblSelfAssessments.setItems(FXCollections.observableArrayList(filtered));
        } catch (BusinessException exception) {
            showError("Error al buscar autoevaluaciones: " + exception.getMessage());
        }
    }

    @FXML
    private void handleClearSearchByName(javafx.event.ActionEvent actionEvent) {
        txtSearchName.clear();
        loadAssessments();
    }

    @FXML
    private void goHome(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("ProjectsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(javafx.event.ActionEvent actionEvent) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(javafx.event.ActionEvent actionEvent) {
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
