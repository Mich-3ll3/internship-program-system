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
import mx.uv.internshipprogramsystem.logic.dto.MonthlyReportContextDTO;
import mx.uv.internshipprogramsystem.logic.dto.PlannedActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IReportDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ReportDAO implements IReportDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDAO.class);

    private static final String SQL_GET_CONTEXT = 
        "SELECT ee.NRC AS nrc, " +
        "ee.profesor_id AS professorId, " +
        "CONCAT_WS(' ', u_prof.nombre, u_prof.apellido_paterno, u_prof.apellido_materno) AS profesorName, " +
        "ee.periodo_escolar AS schoolPeriod, " +
        "org.nombre AS organizationName, " +
        "p.nombre AS projectName, " +
        "p.objetivo_general AS generalObjective, " +
        "p.metodologia AS methodology, " +
        "p.id AS projectId " +
        "FROM ESTUDIANTE est " +
        "JOIN ASIGNACION_PROYECTO ap ON est.usuario_id = ap.estudiante_id " +
        "JOIN PROYECTO p ON ap.proyecto_id = p.id " +
        "JOIN ORGANIZACION_VINCULADA org ON p.organizacion_id = org.id " +
        "JOIN EXPERIENCIA_ESTUDIANTES ee_est ON est.usuario_id = ee_est.estudiante_id AND ee_est.estado = 'ACTIVA' " +
        "JOIN EXPERIENCIA_EDUCATIVA ee ON ee_est.NRC = ee.NRC " +
        "JOIN USUARIO u_prof ON ee.profesor_id = u_prof.id " +
        "WHERE est.usuario_id = ?";

    private static final String SQL_GET_INTERNS_BY_PROJECT = 
        "SELECT CONCAT_WS(' ', u.nombre, u.apellido_paterno, u.apellido_materno) AS nombreCompleto " +
        "FROM ASIGNACION_PROYECTO ap " +
        "JOIN USUARIO u ON ap.estudiante_id = u.id " +
        "WHERE ap.proyecto_id = ?";

    // Actualizada para incluir las horas_planeadas
    private static final String SQL_GET_PLANNED_ACTIVITIES = 
        "SELECT nombre, horas_planeadas FROM ACTIVIDADES_PLAN WHERE proyecto_id = ?";

    @Override
    public boolean registerReport(ReportDTO report) throws BusinessException {
        InputValidator.validateNotNull(report, "ReportDTO no puede ser nulo.");

        String insertReportQuery = "INSERT INTO REPORTE (numero, fecha, observaciones_generales, tipo, estado, estudiante_id, profesor_id, proyecto_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertAdvancesQuery = "INSERT INTO REPORTE_AVANCES (periodo, mes, horas_reportadas, porcentaje_avance, observaciones_particulares, resultados_obtenidos_momento, reporte_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;

        try {
            connection = DataBaseManager.getConnection();
            connection.setAutoCommit(false);

            int generatedReportId;

            try (PreparedStatement preparedStatementReport = connection.prepareStatement(insertReportQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                preparedStatementReport.setInt(1, report.getNumber());
                preparedStatementReport.setDate(2, Date.valueOf(report.getDate()));
                preparedStatementReport.setString(3, report.getGeneralObservations());
                preparedStatementReport.setString(4, report.getType());
                preparedStatementReport.setString(5, report.getStatus());
                preparedStatementReport.setInt(6, report.getStudentId());
                preparedStatementReport.setInt(7, report.getProfessorId());
                preparedStatementReport.setInt(8, report.getProjectId());
                
                preparedStatementReport.executeUpdate();

                try (ResultSet generatedKeys = preparedStatementReport.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedReportId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del reporte generado.");
                    }
                }
            }

            try (PreparedStatement preparedStatementAdvances = connection.prepareStatement(insertAdvancesQuery)) {
                preparedStatementAdvances.setString(1, report.getPeriod());
                preparedStatementAdvances.setInt(2, report.getMonth());
                preparedStatementAdvances.setInt(3, report.getReportedHours());
                preparedStatementAdvances.setString(4, report.getAdvancePercentage());
                preparedStatementAdvances.setString(5, report.getParticularObservations());
                preparedStatementAdvances.setString(6, report.getCurrentResults());
                preparedStatementAdvances.setInt(7, generatedReportId);
                
                preparedStatementAdvances.executeUpdate();
            }

            connection.commit();
            LOGGER.info("Reporte y avances registrados exitosamente con ID: {}", generatedReportId);
            return true;

        } catch (SQLException sqlException) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    LOGGER.error("Error al intentar realizar rollback en la base de datos", rollbackException);
                }
            }
            LOGGER.error("Error crítico registrando el reporte y sus avances", sqlException);
            throw new BusinessException("Error registrando reporte", sqlException);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeException) {
                    LOGGER.error("Error cerrando la conexión a la base de datos", closeException);
                }
            }
        }
    }

    @Override
    public List<ReportDTO> getAllReports() throws BusinessException {
        List<ReportDTO> reports = new ArrayList<>();
        
        String selectQuery = 
            "SELECT r.*, ra.horas_reportadas, ra.resultados_obtenidos_momento, ra.observaciones_particulares " +
            "FROM REPORTE r " +
            "LEFT JOIN REPORTE_AVANCES ra ON r.id = ra.reporte_id";

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

                report.setReportedHours(resultSet.getInt("horas_reportadas"));
                report.setCurrentResults(resultSet.getString("resultados_obtenidos_momento"));
                report.setParticularObservations(resultSet.getString("observaciones_particulares"));

                java.sql.Timestamp reviewTimestamp = resultSet.getTimestamp("fecha_revision");
                if (reviewTimestamp != null) {
                    report.setReviewDate(reviewTimestamp.toLocalDateTime().toLocalDate());
                }

                reports.add(report);
            }

        } catch (SQLException sqlException) {
            LOGGER.error("Error obteniendo todos los reportes con sus avances", sqlException);
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

    public List<PlannedActivityDTO> getProjectActivities(int projectId) {
        List<PlannedActivityDTO> activities = new ArrayList<>();
        
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_PLANNED_ACTIVITIES)) {
            
            preparedStatement.setInt(1, projectId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    PlannedActivityDTO activity = new PlannedActivityDTO();
                    activity.setName(resultSet.getString("nombre"));
                    activity.setPlannedHours(resultSet.getInt("horas_planeadas"));
                    activities.add(activity);
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error al obtener actividades del proyecto ID: {}", projectId, sqlException);
        }
        
        return activities;
    }

    public Optional<MonthlyReportContextDTO> getProjectContextForIntern(int internId) {
        if (internId <= 0) {
            return Optional.empty();
        }

        MonthlyReportContextDTO context = new MonthlyReportContextDTO();
        int projectId = 0;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_CONTEXT)) {
             
            preparedStatement.setInt(1, internId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    context.setNrc(resultSet.getString("nrc"));
                    context.setProfessorId(resultSet.getInt("professorId"));
                    context.setProfessorName(resultSet.getString("profesorName"));
                    context.setSchoolPeriod(resultSet.getString("schoolPeriod"));
                    context.setOrganizationName(resultSet.getString("organizationName"));
                    context.setProjectName(resultSet.getString("projectName"));
                    context.setGeneralObjective(resultSet.getString("generalObjective"));
                    context.setMethodology(resultSet.getString("methodology"));
                    context.setProjectId(resultSet.getInt("projectId"));
                    
                    projectId = resultSet.getInt("projectId");
                } else {
                    LOGGER.warn("No se encontró proyecto o experiencia activa para el practicante ID: {}", internId);
                    return Optional.empty();
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error al consultar el contexto del proyecto en BD: {}", sqlException.getMessage());
            return Optional.empty();
        }

        if (projectId > 0) {
            List<String> internNames = new ArrayList<>();
            try (Connection connection = DataBaseManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SQL_GET_INTERNS_BY_PROJECT)) {
                 
                preparedStatement.setInt(1, projectId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        internNames.add(resultSet.getString("nombreCompleto"));
                    }
                }
                context.setInternNames(internNames);
                
            } catch (SQLException sqlException) {
                LOGGER.error("Error al consultar la lista de compañeros de proyecto: {}", sqlException.getMessage());
            }
        }

        return Optional.of(context);
    }
    
    
    public int getReportCount(int internId, String reportType) {
        int count = 0;
        
        final int PARAM_INTERN_ID = 1;
        final int PARAM_REPORT_TYPE = 2;
        final int COLUMN_INDEX_COUNT = 1;

        String query = "SELECT COUNT(*) FROM REPORTE WHERE estudiante_id = ? AND tipo = ?"; 
        
        try (java.sql.Connection connection = DataBaseManager.getConnection(); 
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(PARAM_INTERN_ID, internId);
            preparedStatement.setString(PARAM_REPORT_TYPE, reportType);
            
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(COLUMN_INDEX_COUNT); 
                }
            }
        } catch (java.sql.SQLException exception) {
            LOGGER.error("Error de SQL al intentar contar los reportes para el intern ID {}. Detalles: {}", internId, exception.getMessage(), exception);
        }
        
        return count;
    }
    
    
    
    @Override
    public int getAccumulatedHours(int internId) {
        int accumulatedHours = 0;
        
        final int PARAM_INTERN_ID = 1;
        final int COLUMN_INDEX_SUM = 1;

        // Hacemos JOIN porque las horas están en AVANCES, pero el ID del intern está en REPORTE
        String query = "SELECT SUM(ra.horas_reportadas) " +
                       "FROM REPORTE_AVANCES ra " +
                       "JOIN REPORTE r ON ra.reporte_id = r.id " +
                       "WHERE r.estudiante_id = ?"; 
        
        try (java.sql.Connection connection = DataBaseManager.getConnection(); 
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(PARAM_INTERN_ID, internId);
            
            try (java.sql.ResultSet resultSet = preparedStatement.executeQuery()) {
                // getInt() devuelve 0 automáticamente si la suma es NULL (cuando no hay reportes aún)
                if (resultSet.next()) {
                    accumulatedHours = resultSet.getInt(COLUMN_INDEX_SUM); 
                }
            }
        } catch (java.sql.SQLException exception) {
            LOGGER.error("Error de SQL al intentar sumar las horas acumuladas para el intern ID {}. Detalles: {}", internId, exception.getMessage(), exception);
        }
        
        return accumulatedHours;
    }
}