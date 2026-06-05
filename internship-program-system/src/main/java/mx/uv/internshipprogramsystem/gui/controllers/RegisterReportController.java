package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import mx.uv.internshipprogramsystem.logic.dto.ActivityPlanDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.managers.ReportManager;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class RegisterReportController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterReportController.class);
    private static final String NOT_AVAILABLE = "N/A";

    @FXML private Button btnHome;
    @FXML private Button btnProjects;
    @FXML private Button btnDocuments;
    @FXML private Button btnReports;
    @FXML private Button btnSelfAssessments;
    @FXML private Button btnExit;

    @FXML private Label lblReportNumber;
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

    @FXML private TableView<ActivityPlanDTO> tblActivities; 
    @FXML private TableColumn<ActivityPlanDTO, String> colActivity;
    @FXML private TableColumn<ActivityPlanDTO, String> colType;
    @FXML private TableColumn<ActivityPlanDTO, String> colWeek1;
    @FXML private TableColumn<ActivityPlanDTO, String> colWeek2;
    @FXML private TableColumn<ActivityPlanDTO, String> colWeek3;
    @FXML private TableColumn<ActivityPlanDTO, String> colWeek4;

    @FXML private TextArea txtResults;
    @FXML private TextArea txtObservations;

    @FXML private Button btnCancel;
    @FXML private Button btnSubmit;

    private ReportManager reportManager;
    
    private static final int WEEK_ONE = 1;
    private static final int WEEK_TWO = 2;
    private static final int WEEK_THREE = 3;
    private static final int WEEK_FOUR = 4;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("Inicializando ventana de Registro de Reporte Mensual.");
        this.reportManager = new ReportManager();

        tblActivities.setEditable(true);

        colActivity.setCellValueFactory(new PropertyValueFactory<>("activityName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        colWeek1.setCellValueFactory(new PropertyValueFactory<>("week1Hours"));
        colWeek1.setCellFactory(TextFieldTableCell.forTableColumn());
        WeekCellEditEventHandler handlerWeekOne = new WeekCellEditEventHandler(WEEK_ONE);
        colWeek1.setOnEditCommit(handlerWeekOne);

        colWeek2.setCellValueFactory(new PropertyValueFactory<>("week2Hours"));
        colWeek2.setCellFactory(TextFieldTableCell.forTableColumn());
        WeekCellEditEventHandler handlerWeekTwo = new WeekCellEditEventHandler(WEEK_TWO);
        colWeek2.setOnEditCommit(handlerWeekTwo);

        colWeek3.setCellValueFactory(new PropertyValueFactory<>("week3Hours"));
        colWeek3.setCellFactory(TextFieldTableCell.forTableColumn());
        WeekCellEditEventHandler handlerWeekThree = new WeekCellEditEventHandler(WEEK_THREE);
        colWeek3.setOnEditCommit(handlerWeekThree);

        colWeek4.setCellValueFactory(new PropertyValueFactory<>("week4Hours"));
        colWeek4.setCellFactory(TextFieldTableCell.forTableColumn());
        WeekCellEditEventHandler handlerWeekFour = new WeekCellEditEventHandler(WEEK_FOUR);
        colWeek4.setOnEditCommit(handlerWeekFour);

        loadReportContextData();
    }

    private void bindContextToInterface(MonthlyReportContextDTO context) {
        lblMajor.setText(context.getMajor());
        lblNrc.setText(context.getNrc());
        lblProfessor.setText(context.getProfessorName());
        lblSchoolPeriod.setText(context.getSchoolPeriod());
        
        lblOrganization.setText(context.getOrganizationName());
        lblProject.setText(context.getProjectName());
        lblGeneralObjective.setText(context.getGeneralObjective());
        lblMethodology.setText(context.getMethodology());

        lblInterns.setText(context.getFormattedInternNames());
        lblAccumulatedHours.setText(context.getFormattedAccumulatedHours());
        lblReportNumber.setText(context.getFormattedReportNumber());
        lblDate.setText(context.getFormattedReportDate());

        if (context.getPlannedActivities() != null) {
            tblActivities.getItems().clear();
            tblActivities.getItems().addAll(context.getPlannedActivities());
        }

        LOGGER.info("Contexto del reporte pre-cargado exitosamente en la vista.");
    }

    private void fillLabelsWithDefaultData() {
        lblMajor.setText(NOT_AVAILABLE);
        lblNrc.setText(NOT_AVAILABLE);
        lblProfessor.setText(NOT_AVAILABLE);
        lblSchoolPeriod.setText(NOT_AVAILABLE);
        lblInterns.setText(NOT_AVAILABLE);
        lblOrganization.setText(NOT_AVAILABLE);
        lblProject.setText(NOT_AVAILABLE);
        lblAccumulatedHours.setText(NOT_AVAILABLE);
        lblDate.setText(NOT_AVAILABLE);
        lblReportNumber.setText("Reporte Inválido");
        lblGeneralObjective.setText(NOT_AVAILABLE);
        lblMethodology.setText(NOT_AVAILABLE);
    }
    
    private void loadReportContextData() {
        Optional<InternDTO> currentInternOptional = UserSessionManager.getCurrentIntern();

        if (currentInternOptional.isEmpty()) {
            LOGGER.error("Fallo de sesión: No hay un practicante activo al intentar abrir un nuevo reporte.");
            fillLabelsWithDefaultData();
            return;
        }

        InternDTO currentIntern = currentInternOptional.get();
        
        try {
            Optional<MonthlyReportContextDTO> contextOptional = reportManager.generateMonthlyContext(currentIntern.getId());

            if (contextOptional.isPresent()) {
                MonthlyReportContextDTO context = contextOptional.get();
                bindContextToInterface(context);
            } else {
                LOGGER.warn("No se pudo obtener el contexto del proyecto para el practicante ID: {}", currentIntern.getId());
                fillLabelsWithDefaultData();
            }
        } catch (ValidationException validationException) {
            LOGGER.error("Error de validación al generar el contexto: {}", validationException.getMessage());
            fillLabelsWithDefaultData();
        }
    }
    
    // --- CLASE INTERNA PARA EL MANEJO DE EDICIÓN DE CELDAS ---
    private class WeekCellEditEventHandler implements EventHandler<CellEditEvent<ActivityPlanDTO, String>> {
        
        private int targetWeek;

        public WeekCellEditEventHandler(int targetWeek) {
            this.targetWeek = targetWeek;
        }

        @Override
        public void handle(CellEditEvent<ActivityPlanDTO, String> event) {
            ActivityPlanDTO selectedActivity = event.getRowValue();
            String newValue = event.getNewValue();

            if (newValue != null && newValue.matches("\\d+")) {
                if (this.targetWeek == WEEK_ONE) {
                    selectedActivity.setWeek1Hours(newValue);
                } else if (this.targetWeek == WEEK_TWO) {
                    selectedActivity.setWeek2Hours(newValue);
                } else if (this.targetWeek == WEEK_THREE) {
                    selectedActivity.setWeek3Hours(newValue);
                } else if (this.targetWeek == WEEK_FOUR) {
                    selectedActivity.setWeek4Hours(newValue);
                }
                LOGGER.info("Horas reales actualizadas correctamente.");
            } else {
                LOGGER.warn("Bloqueo de edición: Valor no numérico detectado.");
                tblActivities.refresh(); 
            }
        }
    } // FIN DE LA CLASE INTERNA

    // --- MÉTODOS DE NAVEGACIÓN FXML ---
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("InternHomeDashboard.fxml");
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info("Redirección a módulo de proyectos.");
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        LOGGER.info("Redirección a módulo de documentos.");
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
        LOGGER.info("Cierre de sesión realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    // --- MÉTODOS DE ACCIÓN DEL FORMULARIO ---
    @FXML
    private void handleBtnCancelClick(ActionEvent event) {
        LOGGER.info("Creación de reporte mensual cancelada por el usuario.");
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void handleBtnSubmitClick(ActionEvent event) {
        LOGGER.info(" INICIANDO PROCESO DE REGISTRO (BOTÓN PRESIONADO) ");

        String resultsText = txtResults.getText();
        String observationsText = txtObservations.getText();

        if (resultsText == null || resultsText.trim().isEmpty()) {
            LOGGER.warn("BLOQUEO DE VISTA: El campo de resultados está vacío.");
            return;
        }

        if (observationsText == null || observationsText.trim().isEmpty()) {
            LOGGER.warn("BLOQUEO DE VISTA: El campo de observaciones está vacío.");
            return;
        }

        Optional<InternDTO> currentInternOptional = UserSessionManager.getCurrentIntern();
        if (currentInternOptional.isEmpty()) {
            LOGGER.error("BLOQUEO DE SESIÓN: No se encontró un practicante activo.");
            return;
        }

        try {
            Optional<MonthlyReportContextDTO> contextOptional = reportManager.generateMonthlyContext(currentInternOptional.get().getId());
            
            if (contextOptional.isEmpty()) {
                LOGGER.error("BLOQUEO DE CONTEXTO: Faltan datos del proyecto en la BD.");
                return;
            }

            MonthlyReportContextDTO context = contextOptional.get();

            ReportDTO newReport = new ReportDTO();
            newReport.setStudentId(currentInternOptional.get().getId());
            newReport.setProjectId(context.getProjectId());
            newReport.setProfessorId(context.getProfessorId());
            newReport.setType(ReportManager.TYPE_MONTHLY);
            newReport.setStatus("pendiente");
            newReport.setDate(java.time.LocalDate.now());
            newReport.setNumber(context.getReportNumber());
            
            newReport.setCurrentResults(resultsText);
            newReport.setParticularObservations(observationsText);
            newReport.setMonth(java.time.LocalDate.now().getMonthValue());
            newReport.setPeriod(context.getSchoolPeriod());
            
            // Lógica de suma de horas de la tabla
            int totalReportedHours = 0;
            for (ActivityPlanDTO activity : tblActivities.getItems()) {
                totalReportedHours += Integer.parseInt(activity.getWeek1Hours());
                totalReportedHours += Integer.parseInt(activity.getWeek2Hours());
                totalReportedHours += Integer.parseInt(activity.getWeek3Hours());
                totalReportedHours += Integer.parseInt(activity.getWeek4Hours());
            }
            
            newReport.setReportedHours(totalReportedHours); 
            newReport.setAdvancePercentage("0%");

            boolean isRegistered = reportManager.registerReport(newReport);

            if (isRegistered) {
                LOGGER.info("====== ÉXITO: REPORTE MENSUAL GUARDADO EN MYSQL ======");
                WindowManagerController.changeView("ReportHomeDashboard.fxml");
            } else {
                LOGGER.error("====== FALLO SILENCIOSO: El Manager devolvió false ======");
            }

        } catch (ValidationException validationException) {
            LOGGER.error("ERROR DE VALIDACIÓN: {}", validationException.getMessage());
        } catch (BusinessException businessException) {
            LOGGER.error("ERROR DE BASE DE DATOS: {}", businessException.getMessage());
        } catch (Exception unexpectedException) {
            LOGGER.error("ERROR INESPERADO: {}", unexpectedException.getMessage(), unexpectedException);
        }
    }
}