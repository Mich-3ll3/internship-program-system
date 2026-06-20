package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.*;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.managers.*;

public class RegisterPartialReportController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterPartialReportController.class);
    private static final String TYPE_PARTIAL_REPORT = "PARCIAL";

    @FXML private Button btnSubmit;
    @FXML private Label lblMajor, lblNrc, lblProfessor, lblSchoolPeriod, lblInterns, lblOrganization, lblProject, lblAccumulatedHours, lblDate, lblGeneralObjective, lblMethodology, lblReportNumber;
    @FXML private TableView<ActivityPlanDTO> tblActivities;
    @FXML private TableColumn<ActivityPlanDTO, String> colActivity, colType, colTotal;
    @FXML private TextArea txtResults, txtObservations;

    private ReportManager reportManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.reportManager = new ReportManager();
        colActivity.setCellValueFactory(new PropertyValueFactory<>("activityName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalHours"));
        loadReportContextData();
    }

    private void loadReportContextData() {
        Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();

        if (currentIntern.isPresent()) {
            try {
                Optional<MonthlyReportContextDTO> context = reportManager.generateMonthlyContext(currentIntern.get().getId());

                if (context.isPresent()) {
                    updateUI(context.get());
                }
            } catch (ValidationException e) {
                LOGGER.error("Error al cargar contexto: {}", e.getMessage());
            }
        }
    }

    private void updateUI(MonthlyReportContextDTO contextDTO) {
        lblMajor.setText(contextDTO.getMajor());
        lblNrc.setText(contextDTO.getNrc());
        lblProfessor.setText(contextDTO.getProfessorName());
        lblSchoolPeriod.setText(contextDTO.getSchoolPeriod());
        lblOrganization.setText(contextDTO.getOrganizationName());
        lblProject.setText(contextDTO.getProjectName());
        lblInterns.setText(contextDTO.getFormattedInternNames());
        lblAccumulatedHours.setText(contextDTO.getFormattedAccumulatedHours());
        lblDate.setText(contextDTO.getFormattedReportDate());
        lblGeneralObjective.setText(contextDTO.getGeneralObjective());
        lblMethodology.setText(contextDTO.getMethodology());
        lblReportNumber.setText("Reporte Parcial 1");
        tblActivities.getItems().addAll(contextDTO.getPlannedActivities());
    }

    @FXML
    private void handleBtnSubmitClick(ActionEvent event) {
        if (txtResults.getText().trim().isEmpty()) {
            showWarning("Los resultados no pueden estar vacíos.");
        } else {
            btnSubmit.setDisable(true);
            try {
                processReportSubmission();
            } catch (Exception e) {
                handleException(e);
            } finally {
                btnSubmit.setDisable(false);
            }
        }
    }

    private void processReportSubmission() throws Exception {
        Optional<InternDTO> currentIntern = UserSessionManager.getCurrentIntern();
        
        if (currentIntern.isPresent()) {
            int studentId = currentIntern.get().getId();
            reportManager.validateReportLimit(studentId, TYPE_PARTIAL_REPORT);
            
            Optional<MonthlyReportContextDTO> context = reportManager.generateMonthlyContext(studentId);
            
            if (context.isPresent()) {
                ReportDTO report = buildReport(studentId, context.get());
                boolean isRegistered = reportManager.registerReport(report);
                
                if (isRegistered) {
                    showSuccess("Reporte registrado.");
                    WindowManagerController.changeView("ReportHomeDashboard.fxml");
                }
            } else {
                showError("Faltan datos del proyecto.");
            }
        } else {
            throw new BusinessException("No hay una sesión de estudiante activa.");
        }
    }

    private ReportDTO buildReport(int studentId, MonthlyReportContextDTO context) {
        ReportDTO report = new ReportDTO();
        report.setStudentId(studentId);
        report.setProjectId(context.getProjectId());
        report.setProfessorId(context.getProfessorId());
        report.setType(TYPE_PARTIAL_REPORT);
        report.setStatus("PENDIENTE");
        report.setDate(LocalDate.now());
        report.setNumber(1);
        report.setCurrentResults(txtResults.getText().trim());
        
        String observations = txtObservations.getText().trim();
        if (observations.isEmpty()) {
            report.setParticularObservations("Sin observaciones.");
        } else {
            report.setParticularObservations(observations);
        }
        
        report.setMonth(LocalDate.now().getMonthValue());
        report.setPeriod(context.getSchoolPeriod());
        report.setReportedHours(calculateTotalHours());
        report.setAdvancePercentage("50%");
        
        return report;
    }

    private int calculateTotalHours() {
        int total = 0;
        for (ActivityPlanDTO activity : tblActivities.getItems()) {
            String totalHoursString = activity.getTotalHours();
            String cleanHours = totalHoursString.replaceAll("[^\\d]", "");
            
            if (!cleanHours.isEmpty()) {
                total = total + Integer.parseInt(cleanHours);
            }
        }
        return total;
    }

    private void handleException(Exception e) {
        if (e instanceof BusinessException) {
            showWarning(e.getMessage());
        } else {
            showError("Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
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
        UserSessionManager.clearSession(); 
        WindowManagerController.changeView("LoginDashboard.fxml"); 
    }
    
    @FXML 
    private void handleBtnCancelClick(ActionEvent event) { 
        WindowManagerController.changeView("ReportHomeDashboard.fxml"); 
    }
}