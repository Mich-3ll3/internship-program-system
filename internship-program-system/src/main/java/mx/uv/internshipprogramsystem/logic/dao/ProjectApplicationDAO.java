package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectApplicationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectApplicationDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectApplicationDAO implements IProjectApplicationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectApplicationDAO.class);

    private static final String INSERT_APPLICATION_QUERY = 
        "INSERT INTO SOLICITUD_PROYECTO (estudiante_id, proyecto_id, prioridad, estado) " +
        "VALUES (?, ?, ?, 'PENDIENTE') " +
        "ON DUPLICATE KEY UPDATE estado = 'PENDIENTE', prioridad = VALUES(prioridad), fecha_solicitud = CURRENT_TIMESTAMP";

    private static final String SELECT_APPLICATIONS_BY_STUDENT = 
        "SELECT sp.estudiante_id, sp.proyecto_id, sp.prioridad, sp.fecha_solicitud, sp.estado, " +
        "p.nombre AS nombre_proyecto, o.nombre AS nombre_organizacion " +
        "FROM SOLICITUD_PROYECTO sp " +
        "INNER JOIN PROYECTO p ON sp.proyecto_id = p.id " +
        "INNER JOIN ORGANIZACION_VINCULADA o ON p.organizacion_id = o.id " +
        "WHERE sp.estudiante_id = ? AND sp.estado != 'CANCELADA'";

    private static final String DELETE_APPLICATION_QUERY = 
        "UPDATE SOLICITUD_PROYECTO SET estado = 'CANCELADA' WHERE estudiante_id = ? AND proyecto_id = ?";

    private static final int MYSQL_DUPLICATE_KEY_CODE = 1062;
    private static final int ZERO_ROWS_AFFECTED = 0;

    @Override
    public boolean createApplication(ProjectApplicationDTO application) throws BusinessException {
        InputValidator.validateNotNull(application, "ProjectApplicationDTO no puede ser nulo.");

        final int PARAM_INTERN_ID = 1;
        final int PARAM_PROJECT_ID = 2;
        final int PARAM_PRIORITY = 3;

        boolean wasCreated = false;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_APPLICATION_QUERY)) {
            
            insertStatement.setInt(PARAM_INTERN_ID, application.getInternId());
            insertStatement.setInt(PARAM_PROJECT_ID, application.getProjectId());
            insertStatement.setInt(PARAM_PRIORITY, application.getPriority()); 

            wasCreated = insertStatement.executeUpdate() > ZERO_ROWS_AFFECTED;
            
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos al crear solicitud", connectionException);
            throw new BusinessException(
                "No se pudo conectar con la base de datos.", 
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error("Error creando solicitud para el estudiante id: {} en el proyecto id: {}", 
                    application.getInternId(), application.getProjectId(), sqlException);
            
            if (sqlException.getErrorCode() == MYSQL_DUPLICATE_KEY_CODE) {
                throw new BusinessException("Ya tienes una postulación activa con esa prioridad o para este proyecto.");
            }
            
            throw new BusinessException("Error al registrar la postulación en el sistema.", sqlException);
        }

        return wasCreated;
    }

    @Override
    public List<ProjectApplicationDTO> getApplicationsByStudent(int internId) throws BusinessException {
        InputValidator.validatePositive(internId, "El ID del estudiante debe ser positivo.");
        
        final int PARAM_INTERN_ID = 1;
        List<ProjectApplicationDTO> applications = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_APPLICATIONS_BY_STUDENT)) {
            
            selectStatement.setInt(PARAM_INTERN_ID, internId);
            
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    ProjectApplicationDTO app = new ProjectApplicationDTO();
                    app.setInternId(resultSet.getInt("estudiante_id"));
                    app.setProjectId(resultSet.getInt("proyecto_id"));
                    app.setPriority(resultSet.getInt("prioridad"));
                    app.setProjectName(resultSet.getString("nombre_proyecto"));
                    app.setOrganizationName(resultSet.getString("nombre_organizacion"));
                    
                    Timestamp date = resultSet.getTimestamp("fecha_solicitud");
                    if (date != null) {
                        app.setApplicationDate(date.toLocalDateTime().toLocalDate().toString());
                    } else {
                        app.setApplicationDate("Fecha no disponible");
                    }
                    
                    app.setStatus(resultSet.getString("estado"));
                    
                    applications.add(app);
                }
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión al buscar postulaciones", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error buscando postulaciones para el estudiante id: {}", internId, sqlException);
            throw new BusinessException("Error al obtener tu lista de postulaciones.", sqlException);
        }

        return applications;
    }

    @Override
    public boolean deleteApplication(int internId, int projectId) throws BusinessException {
        InputValidator.validatePositive(internId, "El ID del estudiante debe ser positivo.");
        InputValidator.validatePositive(projectId, "El ID del proyecto debe ser positivo.");

        final int PARAM_INTERN_ID = 1;
        final int PARAM_PROJECT_ID = 2;

        boolean wasDeleted = false;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(DELETE_APPLICATION_QUERY)) {
            
            deleteStatement.setInt(PARAM_INTERN_ID, internId);
            deleteStatement.setInt(PARAM_PROJECT_ID, projectId);

            wasDeleted = deleteStatement.executeUpdate() > ZERO_ROWS_AFFECTED;
            
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión al eliminar solicitud", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error eliminando solicitud del estudiante id: {} en el proyecto id: {}", 
                    internId, projectId, sqlException);
            throw new BusinessException("Error al cancelar la postulación en el sistema.", sqlException);
        }

        return wasDeleted;
    }
}