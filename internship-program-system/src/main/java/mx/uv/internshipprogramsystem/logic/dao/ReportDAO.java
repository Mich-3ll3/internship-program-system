package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

        String insertQuery =
            "INSERT INTO REPORTE (numero, fecha, observaciones_generales, tipo, estado, ruta_archivo, estudiante_id, profesor_id, proyecto_id) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, report.getNumber());
            preparedStatement.setDate(2, Date.valueOf(report.getDate()));
            preparedStatement.setString(3, report.getGeneralObservations());
            preparedStatement.setString(4, report.getType());
            preparedStatement.setString(5, report.getStatus());
            preparedStatement.setString(6, report.getFilePath());
            preparedStatement.setInt(7, report.getStudentId());
            preparedStatement.setInt(8, report.getProfessorId());
            preparedStatement.setInt(9, report.getProjectId());

            int affectedRows = preparedStatement.executeUpdate();
            LOGGER.info("Reporte registrado con número {}", report.getNumber());
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            LOGGER.error("Error registrando reporte {}", report.getNumber(), sqlException);
            throw new BusinessException("Error registrando reporte " + report.getNumber(), sqlException);
        }
    }

    @Override
    public List<ReportDTO> getAllReports() throws BusinessException {
        List<ReportDTO> reports = new ArrayList<>();
        String selectQuery = "SELECT * FROM REPORTE";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ReportDTO report = new ReportDTO(
                    resultSet.getInt("id"),
                    resultSet.getInt("numero"),
                    resultSet.getDate("fecha").toLocalDate(),
                    resultSet.getString("observaciones_generales"),
                    resultSet.getString("tipo"),
                    resultSet.getString("estado"),
                    resultSet.getString("ruta_archivo"),
                    resultSet.getInt("estudiante_id"),
                    resultSet.getInt("profesor_id"),
                    resultSet.getInt("proyecto_id")
                );

                Timestamp reviewTimestamp = resultSet.getTimestamp("fecha_revision");
                if (reviewTimestamp != null) {
                    report.setReviewDate(reviewTimestamp.toLocalDateTime().toLocalDate());
                }

                reports.add(report);
            }

        } catch (SQLException sqlException) {
            LOGGER.error("Error obteniendo todos los reportes", sqlException);
            throw new BusinessException("Error obteniendo todos los reportes", sqlException);
        }

        return reports;
    }

    @Override
    public boolean updateReportFilePath(int reportId, String filePath) throws BusinessException {
        InputValidator.validatePositive(reportId, "El id del reporte debe ser positivo.");

        String updateQuery = "UPDATE REPORTE SET ruta_archivo = ? WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, filePath);
            preparedStatement.setInt(2, reportId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            LOGGER.error("Error actualizando ruta del archivo para reporte con ID {}", reportId, sqlException);
            throw new BusinessException("Error actualizando ruta del archivo para reporte " + reportId, sqlException);
        }
    }

    @Override
    public boolean evaluateReport(int reportId, String newStatus, String newObservations) throws BusinessException {
        InputValidator.validatePositive(reportId, "El id del reporte debe ser positivo.");
        InputValidator.validateNotEmpty(newStatus, "El estado del reporte es obligatorio.");

        String updateQuery =
            "UPDATE REPORTE SET estado = ?, observaciones_generales = ?, fecha_revision = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, newObservations);
            preparedStatement.setInt(3, reportId);

            int affectedRows = preparedStatement.executeUpdate();
            LOGGER.info("Reporte {} evaluado con estado {}", reportId, newStatus);
            return affectedRows > 0;

        } catch (SQLException sqlException) {
            LOGGER.error("Error evaluando reporte {}", reportId, sqlException);
            throw new BusinessException("Error evaluando reporte con id " + reportId, sqlException);
        }
    }

    @Override
    public Optional<ReportDTO> getReportByStudent(int studentId) throws BusinessException {
        InputValidator.validatePositive(studentId, "El id del estudiante debe ser positivo.");

        String selectQuery = "SELECT * FROM REPORTE WHERE estudiante_id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setInt(1, studentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    LOGGER.warn("No se encontró reporte para estudiante {}", studentId);
                    return Optional.empty();
                }

                LOGGER.info("Reporte encontrado para estudiante {}", studentId);
                return Optional.of(buildReport(resultSet));
            }

        } catch (SQLException sqlException) {
            LOGGER.error("Error consultando reporte para estudiante {}", studentId, sqlException);
            throw new BusinessException("Error consultando reporte para estudiante id " + studentId, sqlException);
        }
    }

    private ReportDTO buildReport(ResultSet resultSet) throws SQLException {
        ReportDTO report = new ReportDTO(
            resultSet.getInt("id"),
            resultSet.getInt("numero"),
            resultSet.getDate("fecha").toLocalDate(),
            resultSet.getString("observaciones_generales"),
            resultSet.getString("tipo"),
            resultSet.getString("estado"),
            resultSet.getString("ruta_archivo"),
            resultSet.getInt("estudiante_id"),
            resultSet.getInt("profesor_id"),
            resultSet.getInt("proyecto_id")
        );

        Timestamp reviewTimestamp = resultSet.getTimestamp("fecha_revision");
        if (reviewTimestamp != null) {
            report.setReviewDate(reviewTimestamp.toLocalDateTime().toLocalDate());
        }

        return report;
    }

    public List<String> getAllProjects() throws BusinessException {
        List<String> projects = new ArrayList<>();
        String selectQuery = "SELECT nombre FROM PROYECTO";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                projects.add(resultSet.getString("nombre"));
            }

        } catch (SQLException sqlException) {
            throw new BusinessException("Error al obtener proyectos: " + sqlException.getMessage());
        }

        return projects;
    }
}
