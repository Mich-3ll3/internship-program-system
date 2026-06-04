package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ReportManager {

    private final ReportDAO reportDAO;

    public ReportManager() {
        this.reportDAO = new ReportDAO();
    }

    public boolean registerReport(ReportDTO report) throws BusinessException {
        InputValidator.validateNotNull(report, "El reporte no puede ser nulo.");
        return reportDAO.registerReport(report);
    }

    public List<ReportDTO> getAllReports() throws BusinessException {
        return reportDAO.getAllReports();
    }

    public boolean updateReportFilePath(int reportId, String filePath) throws BusinessException {
        InputValidator.validatePositive(reportId, "El ID del reporte debe ser positivo.");
        InputValidator.validateNotEmpty(filePath, "La ruta del archivo no puede estar vacía.");
        return reportDAO.updateReportFilePath(reportId, filePath);
    }

    public boolean evaluateReport(int reportId, String newStatus, String newObservations) throws BusinessException {
        InputValidator.validatePositive(reportId, "El ID del reporte debe ser positivo.");
        InputValidator.validateNotEmpty(newStatus, "El estado no puede estar vacío.");
        return reportDAO.evaluateReport(reportId, newStatus, newObservations);
    }

    public Optional<ReportDTO> getReportByStudent(int studentId) throws BusinessException {
        InputValidator.validatePositive(studentId, "El ID del estudiante debe ser positivo.");
        return reportDAO.getReportByStudent(studentId);
    }

    public List<String> getAllProjects() throws BusinessException {
        return reportDAO.getAllProjects();
    }
}
