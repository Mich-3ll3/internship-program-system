package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.dto.PlannedActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IReportDAO {

    boolean registerReport(ReportDTO report) throws BusinessException;

    boolean evaluateReport(int reportId, String newStatus, String newObservations)
            throws BusinessException;
    
    List<ReportDTO> getAllReports() throws BusinessException;
    
    boolean updateReportFilePath(int reportId, String filePath) 
            throws BusinessException;

    Optional<ReportDTO> getReportByStudent(int studentId) throws BusinessException;

    List<String> getAllProjects() throws BusinessException;

    List<PlannedActivityDTO> getProjectActivities(int projectId);

    Optional<MonthlyReportContextDTO> getProjectContextForIntern(int internId);

    int getReportCount(int internId, String reportType);

    int getAccumulatedHours(int internId);
}