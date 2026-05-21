package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IReportDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ReportDAO implements IReportDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDAO.class);

    @Override
    public boolean registerReport(ReportDTO report) throws BusinessException {
        InputValidator.validateNotNull(report, "ReportDTO no puede ser nulo.");
        String insertReportById =
            "INSERT INTO REPORTE (numero, fecha, observaciones_generales, tipo, "
            + "estado, ruta_archivo, estudiante_id, profesor_id, proyecto_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertReportStatement =
                 connection.prepareStatement(insertReportById)) {
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
            LOGGER.info("Reporte registrado con número {}", report.getNumber());
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            LOGGER.error("Error registrando reporte {}", report.getNumber(), sqlException);
            throw new BusinessException(
                "Error registrando reporte " + report.getNumber(),
                sqlException
            );
        }
    }

    @Override
    public boolean evaluateReport(
            int reportId,
            String newStatus,
            String newObservations
    ) throws BusinessException {
        InputValidator.validatePositive(reportId, "El id del reporte debe ser positivo.");
        InputValidator.validateNotEmpty(newStatus, "El estado del reporte es obligatorio.");
        String updateReportQuery =
            "UPDATE REPORTE SET estado = ?, observaciones_generales = ?, "
            + "fecha_revision = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateReportStatement =
                 connection.prepareStatement(updateReportQuery)) {
            updateReportStatement.setString(1, newStatus);
            updateReportStatement.setString(2, newObservations);
            updateReportStatement.setInt(3, reportId);

            int affectedRows = updateReportStatement.executeUpdate();
            LOGGER.info("Reporte {} evaluado con estado {}", reportId, newStatus);
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            LOGGER.error("Error evaluando reporte {}", reportId, sqlException);
            throw new BusinessException(
                "Error evaluando reporte con id " + reportId,
                sqlException
            );
        }
    }

    @Override
    public Optional<ReportDTO> getReportByStudent(int studentId) throws BusinessException {
        InputValidator.validatePositive(studentId, "El id del estudiante debe ser positivo.");
        String selectReportById = "SELECT * FROM REPORTE WHERE estudiante_id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectReport =
                 connection.prepareStatement(selectReportById)) {
            statementSelectReport.setInt(1, studentId);

            try (ResultSet resultSetReport = statementSelectReport.executeQuery()) {
                if (!resultSetReport.next()) {
                    LOGGER.warn("No se encontró reporte para estudiante {}", studentId);
                    return Optional.empty();
                }

                LOGGER.info("Reporte encontrado para estudiante {}", studentId);
                return Optional.of(buildReport(resultSetReport));
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error consultando reporte para estudiante {}", studentId, sqlException);
            throw new BusinessException(
                "Error consultando reporte para estudiante id " + studentId,
                sqlException
            );
        }
    }

    private ReportDTO buildReport(ResultSet resultSetReport) throws SQLException {
        ReportDTO report = new ReportDTO(
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
        Timestamp reviewTimestamp = resultSetReport.getTimestamp("fecha_revision");

        if (reviewTimestamp != null) {
            report.setReviewDate(reviewTimestamp.toLocalDateTime().toLocalDate());
        }

        return report;
    }
}
