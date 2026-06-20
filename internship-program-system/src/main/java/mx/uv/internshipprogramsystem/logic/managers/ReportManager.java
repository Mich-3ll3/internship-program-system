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
    
    public static final String TYPE_MONTHLY = "Mensual";
    public static final String TYPE_PARTIAL = "Parcial";
    public static final String TYPE_FINAL = "Final";

    private static final int MAX_MONTHLY_REPORTS = 6;
    private static final int MAX_PARTIAL_REPORTS = 1;
    private static final int MAX_FINAL_REPORTS = 1;
    private static final int MIN_VALID_ID = 1;
    private static final int NEXT_REPORT_INCREMENT = 1;

    private static final String MSG_NULL_REPORT = "El reporte no puede ser nulo.";
    private static final String MSG_INVALID_ID = "El ID debe ser mayor a cero.";
    private static final String MSG_EMPTY_FILE_PATH = "La ruta del archivo no puede estar vacía.";
    private static final String MSG_EMPTY_STATUS = "El estado no puede estar vacío.";
    private static final String MSG_NULL_TYPE = "El tipo de reporte no puede ser nulo.";

    private final ReportDAO reportDAO;

    public ReportManager() {
        this.reportDAO = new ReportDAO();
    }

    public boolean registerReport(ReportDTO report) throws BusinessException {
        InputValidator.validateNotNull(report, MSG_NULL_REPORT);
        InputValidator.validateNotEmpty(report.getPeriod(), "El periodo escolar es obligatorio.");
        InputValidator.validateNotEmpty(report.getCurrentResults(), "Los resultados obtenidos son obligatorios.");

        LOGGER.info("Iniciando registro de nuevo reporte para el estudiante ID: {}", report.getStudentId());
        
        return reportDAO.registerReport(report);
    }

    public void validateReportLimit(int studentId, String reportType) throws BusinessException {
        int currentCount = reportDAO.countReportsByType(studentId, reportType);
        
        if (TYPE_PARTIAL.equalsIgnoreCase(reportType)) {
            if (currentCount >= MAX_PARTIAL_REPORTS) {
                throw new BusinessException("El límite ha sido alcanzado. Solo se permite " + MAX_PARTIAL_REPORTS + " reporte " + TYPE_PARTIAL.toLowerCase() + " por periodo.");
            }
        }
        
        if (TYPE_MONTHLY.equalsIgnoreCase(reportType)) {
            if (currentCount >= MAX_MONTHLY_REPORTS) {
                throw new BusinessException("El límite ha sido alcanzado. Solo se permiten " + MAX_MONTHLY_REPORTS + " reportes " + TYPE_MONTHLY.toLowerCase() + "es por periodo.");
            }
        }
    }

    public Optional<MonthlyReportContextDTO> generateMonthlyContext(int internId) throws ValidationException {
        InputValidator.validatePositive(internId, MSG_INVALID_ID);

        LOGGER.info("Iniciando generación de contexto de reporte para intern ID: {}", internId);

        Optional<MonthlyReportContextDTO> contextOptional = reportDAO.getProjectContextForIntern(internId);

        if (!contextOptional.isPresent()) {
            LOGGER.warn("No se pudo generar el contexto: Faltan datos del proyecto.");
            return Optional.empty();
        }

        MonthlyReportContextDTO context = contextOptional.get();

        List<PlannedActivityDTO> plannedActivities = reportDAO.getProjectActivities(context.getProjectId());
        List<ActivityPlanDTO> tableRows = new ArrayList<>();
        
        for (int i = 0; i < plannedActivities.size(); i++) {
            PlannedActivityDTO activity = plannedActivities.get(i);
            
            String formattedName = activity.getName() + " (Plan: " + activity.getPlannedHours() + " hrs)";
            
            ActivityPlanDTO realRow = new ActivityPlanDTO();
            realRow.setActivityName(formattedName);
            realRow.setTotalHours("0"); 
            
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

    public List<ReportDTO> getAllReports() throws BusinessException {
        return reportDAO.getAllReports();
    }

    public boolean updateReportFilePath(int reportId, String filePath) throws BusinessException {
        InputValidator.validatePositive(reportId, MSG_INVALID_ID);
        InputValidator.validateNotEmpty(filePath, MSG_EMPTY_FILE_PATH);
        return reportDAO.updateReportFilePath(reportId, filePath);
    }

    public boolean evaluateReport(int reportId, String newStatus, String newObservations) throws BusinessException {
        InputValidator.validatePositive(reportId, MSG_INVALID_ID);
        InputValidator.validateNotEmpty(newStatus, MSG_EMPTY_STATUS);
        return reportDAO.evaluateReport(reportId, newStatus, newObservations);
    }
}