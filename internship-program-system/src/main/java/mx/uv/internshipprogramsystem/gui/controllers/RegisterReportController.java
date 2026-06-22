package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;

import mx.uv.internshipprogramsystem.logic.dto.ActivityPlanDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.managers.ReportManager;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

import mx.uv.internshipprogramsystem.gui.utils.LengthFilter;

public class RegisterReportController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterReportController.class);
    private static final String NOT_AVAILABLE = "N/A";
    private static final String REPORT_TYPE = "MENSUAL";

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

        IntegerCellFactory cellFactory = new IntegerCellFactory();

        colWeek1.setCellValueFactory(new PropertyValueFactory<>("week1Hours"));
        colWeek1.setCellFactory(cellFactory);
        WeekCellEditEventHandler handlerWeekOne = new WeekCellEditEventHandler(WEEK_ONE);
        colWeek1.setOnEditCommit(handlerWeekOne);

        colWeek2.setCellValueFactory(new PropertyValueFactory<>("week2Hours"));
        colWeek2.setCellFactory(cellFactory);
        WeekCellEditEventHandler handlerWeekTwo = new WeekCellEditEventHandler(WEEK_TWO);
        colWeek2.setOnEditCommit(handlerWeekTwo);

        colWeek3.setCellValueFactory(new PropertyValueFactory<>("week3Hours"));
        colWeek3.setCellFactory(cellFactory);
        WeekCellEditEventHandler handlerWeekThree = new WeekCellEditEventHandler(WEEK_THREE);
        colWeek3.setOnEditCommit(handlerWeekThree);

        colWeek4.setCellValueFactory(new PropertyValueFactory<>("week4Hours"));
        colWeek4.setCellFactory(cellFactory);
        WeekCellEditEventHandler handlerWeekFour = new WeekCellEditEventHandler(WEEK_FOUR);
        colWeek4.setOnEditCommit(handlerWeekFour);

        loadReportContextData();
        
        LengthFilter resultsLengthFilter = new LengthFilter(240);
        TextFormatter<String> resultsFormatter = new TextFormatter<>(resultsLengthFilter);
        txtResults.setTextFormatter(resultsFormatter);

        LengthFilter observationsLengthFilter = new LengthFilter(240);
        TextFormatter<String> observationsFormatter = new TextFormatter<>(observationsLengthFilter);
        txtObservations.setTextFormatter(observationsFormatter);
        
        tblActivities.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        LOGGER.info("Cierre de sesión realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void handleBtnCancelClick(ActionEvent event) {
        LOGGER.info("Creación de reporte cancelada por el usuario.");
        WindowManagerController.changeView("ReportHomeDashboard.fxml");
    }

    @FXML
    private void handleBtnSubmitClick(ActionEvent event) {
        LOGGER.info("INICIANDO PROCESO DE REGISTRO (BOTÓN PRESIONADO)");

        String resultsText = txtResults.getText();
        String observationsText = txtObservations.getText();

        if (resultsText == null || resultsText.trim().isEmpty()) {
            showWarning("El campo de resultados obtenidos no puede estar vacío.");
            return;
        }

        Optional<InternDTO> currentInternOptional = UserSessionManager.getCurrentIntern();
        if (currentInternOptional.isEmpty()) {
            showError("No se pudo identificar la sesión activa del estudiante.");
            return;
        }

        btnSubmit.setDisable(true);

        try {
            int studentId = currentInternOptional.get().getId();

            reportManager.validateReportLimit(studentId, REPORT_TYPE);

            Optional<MonthlyReportContextDTO> contextOptional = reportManager.generateMonthlyContext(studentId);
            if (contextOptional.isEmpty()) {
                showError("Faltan datos del proyecto en la base de datos para generar el reporte.");
                return;
            }
            
            MonthlyReportContextDTO context = contextOptional.get();

            ReportDTO newReport = new ReportDTO();
            newReport.setStudentId(studentId);
            newReport.setProjectId(context.getProjectId());
            newReport.setProfessorId(context.getProfessorId());
            newReport.setType(REPORT_TYPE);
            newReport.setStatus("PENDIENTE");
            newReport.setDate(LocalDate.now());
            newReport.setNumber(context.getReportNumber());
            
            newReport.setCurrentResults(resultsText.trim());
            
            if (observationsText != null && !observationsText.trim().isEmpty()) {
                newReport.setParticularObservations(observationsText.trim());
            } else {
                newReport.setParticularObservations("Sin observaciones particulares.");
            }
            
            newReport.setMonth(LocalDate.now().getMonthValue());
            newReport.setPeriod(context.getSchoolPeriod());
            
            int totalReportedHours = 0;
            for (ActivityPlanDTO activity : tblActivities.getItems()) {
                try {
                    totalReportedHours += Integer.parseInt(activity.getWeek1Hours());
                    totalReportedHours += Integer.parseInt(activity.getWeek2Hours());
                    totalReportedHours += Integer.parseInt(activity.getWeek3Hours());
                    totalReportedHours += Integer.parseInt(activity.getWeek4Hours());
                } catch (NumberFormatException e) {
                    LOGGER.warn("Una de las celdas tiene formato inválido. Ignorando cálculo parcial de esa celda.");
                }
            }
            newReport.setReportedHours(totalReportedHours); 
            newReport.setAdvancePercentage("0%");
            
            newReport.setActivities(new java.util.ArrayList<>(tblActivities.getItems()));

            boolean isRegistered = reportManager.registerReport(newReport);

            if (isRegistered) {
                showSuccess("El reporte mensual se registró correctamente.");
                WindowManagerController.changeView("ReportHomeDashboard.fxml");
            } else {
                showError("Ocurrió un problema interno al intentar guardar el reporte.");
            }

        } catch (Exception exception) {
            if (exception instanceof BusinessException) {
                showWarning(exception.getMessage());
            } else {
                showError("Ocurrió un error inesperado al procesar el reporte: " + exception.getMessage());
                LOGGER.error("ERROR INESPERADO: {}", exception.getMessage(), exception);
            }
        } finally {
            btnSubmit.setDisable(false);
        }
    }
    
    public class IntegerFilter implements java.util.function.UnaryOperator<TextFormatter.Change> {
        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            TextFormatter.Change validChange = null;
            String newText = change.getControlNewText();
            
            if (newText.matches("\\d*")) {
                validChange = change;
            }
            
            return validChange;
        }
    }

    public class IntegerEditingCell extends javafx.scene.control.cell.TextFieldTableCell<ActivityPlanDTO, String> {
        public IntegerEditingCell() {
            super(new javafx.util.converter.DefaultStringConverter());
        }

        @Override
        public void startEdit() {
            super.startEdit();
            
            if (getGraphic() instanceof TextField) {
                TextField textField = (TextField) getGraphic();
                IntegerFilter filter = new IntegerFilter();
                TextFormatter<String> formatter = new TextFormatter<>(filter);
                textField.setTextFormatter(formatter);
            }
        }
    }

    public class IntegerCellFactory implements javafx.util.Callback<TableColumn<ActivityPlanDTO, String>, TableCell<ActivityPlanDTO, String>> {
        @Override
        public TableCell<ActivityPlanDTO, String> call(TableColumn<ActivityPlanDTO, String> param) {
            return new IntegerEditingCell();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}