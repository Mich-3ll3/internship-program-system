package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IReportDAO {

    boolean registerReport(ReportDTO report) throws BusinessException;

    boolean evaluateReport(int reportId, String newStatus, String newObservations) throws BusinessException;

    ReportDTO getReportByStudent(int studentId) throws BusinessException;
}
