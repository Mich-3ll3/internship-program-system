package mx.uv.internshipprogramsystem.logic.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ActivityPlanDTO;
import mx.uv.internshipprogramsystem.logic.dto.PlannedActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class ReportManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportManager.class);
    
    public static final String TYPE_MONTHLY = "mensual";
    public static final String TYPE_PARTIAL = "parcial";
    public static final String TYPE_FINAL = "final";

    private static final int MAX_MONTHLY_REPORTS = 6;
    private static final int MAX_PARTIAL_REPORTS = 1;
    private static final int MAX_FINAL_REPORTS = 1;

    private static final String MSG_NULL_REPORT = "El reporte no puede ser nulo.";
    private static final String MSG_INVALID_ID = "El ID debe ser mayor a cero.";
    private static final String MSG_EMPTY_FILE_PATH = "La ruta del archivo no puede estar vacía.";
    private static final String MSG_EMPTY_STATUS = "El estado no puede estar vacío.";
    private static final String MSG_NULL_TYPE = "El tipo de reporte no puede ser nulo.";

    private static final String DEFAULT_REAL_HOURS = "0";
    private static final String EMPTY_TIME_TYPE = "";
    private static final int DEFAULT_INITIAL_REPORT_NUMBER = 1;
    private static final int DEFAULT_INITIAL_ACCUMULATED_HOURS = 0;
    private static final int NEXT_REPORT_INCREMENT = 1;

    private final ReportDAO reportDAO;

    public ReportManager() {
        this.reportDAO = new ReportDAO();
    }

    public boolean registerReport(ReportDTO report) throws BusinessException {
        InputValidator.validateNotNull(report, MSG_NULL_REPORT);
        
        InputValidator.validateNotEmpty(report.getPeriod(), "El periodo escolar es obligatorio.");
        InputValidator.validateNotEmpty(report.getCurrentResults(), "Los resultados obtenidos son obligatorios.");

        LOGGER.info("Iniciando registro de nuevo reporte para el intern ID: {}", report.getStudentId());
        
        return reportDAO.registerReport(report);
    }

    public List<ReportDTO> getAllReports() throws BusinessException {
        LOGGER.info("Consultando la lista completa de reportes.");
        return reportDAO.getAllReports();
    }

    public boolean updateReportFilePath(int reportId, String filePath) throws BusinessException {
        InputValidator.validatePositive(reportId, MSG_INVALID_ID);
        InputValidator.validateNotEmpty(filePath, MSG_EMPTY_FILE_PATH);
        LOGGER.info("Actualizando ruta de archivo para el reporte ID: {}", reportId);
        return reportDAO.updateReportFilePath(reportId, filePath);
    }

    public boolean evaluateReport(int reportId, String newStatus, String newObservations) throws BusinessException {
        InputValidator.validatePositive(reportId, MSG_INVALID_ID);
        InputValidator.validateNotEmpty(newStatus, MSG_EMPTY_STATUS);
        LOGGER.info("Evaluando reporte ID: {} con estado: {}", reportId, newStatus);
        return reportDAO.evaluateReport(reportId, newStatus, newObservations);
    }

    public Optional<ReportDTO> getReportByIntern(int internId) throws BusinessException {
        InputValidator.validatePositive(internId, MSG_INVALID_ID);
        LOGGER.info("Consultando reporte para el intern ID: {}", internId);
        return reportDAO.getReportByStudent(internId); 
    }

    public List<String> getAllProjects() throws BusinessException {
        LOGGER.info("Consultando nombres de todos los proyectos vinculados.");
        return reportDAO.getAllProjects();
    }

    public boolean canSubmitReport(int internId, String type) throws ValidationException {
        InputValidator.validatePositive(internId, MSG_INVALID_ID);
        InputValidator.validateNotEmpty(type, MSG_NULL_TYPE);

        int currentCount = 0; 

        switch (type) {
            case TYPE_MONTHLY:
                return currentCount < MAX_MONTHLY_REPORTS;
            case TYPE_PARTIAL:
                return currentCount < MAX_PARTIAL_REPORTS;
            case TYPE_FINAL:
                return currentCount < MAX_FINAL_REPORTS;
            default:
                LOGGER.warn("Tipo de reporte no reconocido evaluado para el intern ID: {}", internId);
                return false;
        }
    }

    public Optional<MonthlyReportContextDTO> generateMonthlyContext(int internId) throws ValidationException {
        InputValidator.validatePositive(internId, MSG_INVALID_ID);

        LOGGER.info("Iniciando generación de contexto de reporte mensual para intern ID: {}", internId);

        Optional<MonthlyReportContextDTO> contextOptional = reportDAO.getProjectContextForIntern(internId);

        if (contextOptional.isEmpty()) {
            LOGGER.warn("No se pudo generar el contexto: Faltan datos del proyecto en BD.");
            return Optional.empty();
        }

        MonthlyReportContextDTO context = contextOptional.get();

        List<PlannedActivityDTO> plannedActivities = reportDAO.getProjectActivities(context.getProjectId());
        List<ActivityPlanDTO> tableRows = new ArrayList<>();
        
        for (PlannedActivityDTO activity : plannedActivities) {
            
            String formattedName = activity.getName() + " (Plan: " + activity.getPlannedHours() + " hrs)";
            
            ActivityPlanDTO realRow = new ActivityPlanDTO();
            realRow.setActivityName(formattedName); 
            realRow.setWeek1Hours(DEFAULT_REAL_HOURS);
            realRow.setWeek2Hours(DEFAULT_REAL_HOURS);
            realRow.setWeek3Hours(DEFAULT_REAL_HOURS);
            realRow.setWeek4Hours(DEFAULT_REAL_HOURS);
            
            tableRows.add(realRow);
        }
        
        context.setPlannedActivities(tableRows);

        int existingReportsCount = reportDAO.getReportCount(internId, TYPE_MONTHLY);
        int nextReportNumber = existingReportsCount + NEXT_REPORT_INCREMENT;
        context.setReportNumber(nextReportNumber);

        int realAccumulatedHours = reportDAO.getAccumulatedHours(internId);
        context.setAccumulatedHours(realAccumulatedHours);

        return Optional.of(context);
    }
}