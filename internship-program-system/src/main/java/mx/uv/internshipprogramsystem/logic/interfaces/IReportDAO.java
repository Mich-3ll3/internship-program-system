package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ReportException;

public interface IReportDAO {
    boolean registerReport(ReportDTO report) throws ReportException;
    boolean evaluateReport(int reportId, String newStatus, String newObservations) throws ReportException;
    ReportDTO getReportByStudent(int studentId) throws ReportException;
}
