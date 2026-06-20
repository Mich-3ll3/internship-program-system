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

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnSelfAssessments;
    @FXML private Button btnExit;

    @FXML private Label lblMajor;
    @FXML private Label lblNrc;
    @FXML private Label lblProfessor;
    @FXML private Label lblSchoolPeriod;
    @FXML private Label lblInterns;
    @FXML private Label lblOrganization;
    @FXML private Label lblProject;
    @FXML private Label lblAccumulatedHours;
    @FXML private Label lblDate;
    @FXML private Label lblGeneralObjective;
    @FXML private Label lblMethodology;
    @FXML private Label lblReportNumber;

    @FXML private TableView<ActivityPlanDTO> tblActivities;
    @FXML private TableColumn<ActivityPlanDTO, String> colActivity;
    @FXML private TableColumn<ActivityPlanDTO, String> colType;
    @FXML private TableColumn<ActivityPlanDTO, String> colTotal;

    @FXML private TextArea txtResults;
    @FXML private TextArea txtObservations;
    @FXML private Button btnSubmit;
    @FXML private Button btnCancel;

    private ReportManager reportManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Inicializando ventana de Registro de Reporte Parcial.");
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
                    MonthlyReportContextDTO c = context.get();

                    lblMajor.setText(c.getMajor());
                    lblNrc.setText(c.getNrc());
                    lblProfessor.setText(c.getProfessorName());
                    lblSchoolPeriod.setText(c.getSchoolPeriod());
                    lblOrganization.setText(c.getOrganizationName());
                    lblProject.setText(c.getProjectName());
                    lblInterns.setText(c.getFormattedInternNames());
                    lblAccumulatedHours.setText(c.getFormattedAccumulatedHours());
                    lblDate.setText(c.getFormattedReportDate());
                    lblGeneralObjective.setText(c.getGeneralObjective());
                    lblMethodology.setText(c.getMethodology());
                    lblReportNumber.setText(c.getFormattedReportNumber());

                    tblActivities.getItems().addAll(c.getPlannedActivities());
                }
            } catch (ValidationException e) {
                LOGGER.error("Error al cargar contexto: {}", e.getMessage());
            }
        }
    }

    @FXML
    private void handleBtnSubmitClick(ActionEvent event) {
        if (txtResults.getText().trim().isEmpty()) {
            showWarning("Los resultados no pueden estar vacíos.");
            return;
        }

        btnSubmit.setDisable(true);

        try {
            int studentId = UserSessionManager.getCurrentIntern().get().getId();
            reportManager.validateReportLimit(studentId, TYPE_PARTIAL_REPORT);

            int totalHours = 0;
            for (ActivityPlanDTO activity : tblActivities.getItems()) {
                totalHours += Integer.parseInt(activity.getTotalHours());
            }

            ReportDTO report = new ReportDTO();
            report.setStudentId(studentId);
            report.setType(TYPE_PARTIAL_REPORT);
            report.setStatus("PENDIENTE");
            report.setDate(LocalDate.now());
            report.setNumber(1);
            report.setCurrentResults(txtResults.getText().trim());
            report.setParticularObservations(txtObservations.getText().trim());
            report.setReportedHours(totalHours);

            if (reportManager.registerReport(report)) {
                showSuccess("Reporte parcial registrado.");
                WindowManagerController.changeView("ReportHomeDashboard.fxml");
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                showWarning(e.getMessage());
            } else {
                showError("Error: " + e.getMessage());
            }
        } finally {
            btnSubmit.setDisable(false);
        }
    }

    @FXML
    private void goHome(ActionEvent e) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent e) {
        WindowManagerController.changeView("ProjectsDashboard.fxml");
    }

    @FXML
    private void goDocumentsModule(ActionEvent e) {
        WindowManagerController.changeView("DocumentsDashboard.fxml");
    }

    @FXML
    private void goReportsModule(ActionEvent e) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void goSelfAssessmentsModule(ActionEvent e) {
        WindowManagerController.changeView("SelfAssessmentHomeDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent e) {
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void handleBtnCancelClick(ActionEvent e) {
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    private void showError(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR, m);
        a.showAndWait();
    }

    private void showWarning(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING, m);
        a.showAndWait();
    }

    private void showSuccess(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, m);
        a.showAndWait();
    }
}