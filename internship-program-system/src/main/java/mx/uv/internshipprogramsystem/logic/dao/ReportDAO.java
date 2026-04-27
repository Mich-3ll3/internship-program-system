package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.interfaces.IReport;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ReportException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO implements IReport {

    @Override
    public boolean registerReport(ReportDTO report) throws ReportException {
        boolean operationSuccessful = false;
        String insertReportById = "INSERT INTO REPORTE (numero, fecha, observaciones_generales, tipo, estado, ruta_archivo, estudiante_id, profesor_id, proyecto_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement insertReportStatement = Connection.prepareStatement(insertReportById)) {

            insertReportStatement.setInt(1, report.getNumber());
            insertReportStatement.setDate(2, Date.valueOf(report.getDate()));
            insertReportStatement.setString(3, report.getGeneralObservations());
            insertReportStatement.setString(4, report.getType());
            insertReportStatement.setString(5, report.getStatus());
            insertReportStatement.setString(6, report.getFilePath());
            insertReportStatement.setInt(7, report.getStudentId());
            insertReportStatement.setInt(8, report.getProfessorId());
            insertReportStatement.setInt(9, report.getProjectId());

            int affectedRows = insertReportStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ReportException("Error registering report", exception);
        }
        return operationSuccessful;
    }

    @Override
    public boolean evaluateReport(int reportId, String newStatus, String newObservations) throws ReportException {
        boolean operationSuccessful = false;
        String updateReportQuery = "UPDATE REPORTE SET estado = ?, observaciones_generales = ?, fecha_revision = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement updateReportStatement = Connection.prepareStatement(updateReportQuery)) {

            updateReportStatement.setString(1, newStatus);
            updateReportStatement.setString(2, newObservations);
            updateReportStatement.setInt(3, reportId);

            int affectedRows = updateReportStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ReportException("Error evaluating report with id " + reportId, exception);
        }
        return operationSuccessful;
    }

    @Override
    public ReportDTO getReportByStudent(int studentId) throws ReportException {
        ReportDTO reportResult = null;
        String selectReportById = "SELECT * FROM REPORTE WHERE estudiante_id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectReport = Connection.prepareStatement(selectReportById)) {

            statementSelectReport.setInt(1, studentId);
            try (ResultSet resultSetReport = statementSelectReport.executeQuery()) {
                if (resultSetReport.next()) {
                    reportResult = new ReportDTO(
                        resultSetReport.getInt("id"),
                        resultSetReport.getInt("numero"),
                        resultSetReport.getDate("fecha").toLocalDate(),
                        resultSetReport.getString("observaciones_generales"),
                        resultSetReport.getString("tipo"),
                        resultSetReport.getString("estado"),
                        resultSetReport.getString("ruta_archivo"),
                        resultSetReport.getInt("estudiante_id"),
                        resultSetReport.getInt("profesor_id"),
                        resultSetReport.getInt("proyecto_id")
                    );
                    reportResult.setReviewDate(resultSetReport.getTimestamp("fecha_revision") != null
                            ? resultSetReport.getTimestamp("fecha_revision").toLocalDateTime().toLocalDate()
                            : null);
                }
            }
        } catch (SQLException exception) {
            throw new ReportException("Error consulting report for student id " + studentId, exception);
        }
        return reportResult;
    }
}
