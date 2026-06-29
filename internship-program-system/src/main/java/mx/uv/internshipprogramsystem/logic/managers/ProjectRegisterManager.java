package mx.uv.internshipprogramsystem.logic.managers;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.ProjectActivityValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProjectValidator;

public class ProjectRegisterManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectRegisterManager.class);

    private final ProjectDAO projectDAO;
    private final ProjectActivityDAO activityDAO;

    public ProjectRegisterManager() {
        projectDAO = new ProjectDAO();
        activityDAO = new ProjectActivityDAO();
    }

    public boolean registerProject(
            ProjectDTO project,
            List<ProjectActivityDTO> activities
    ) throws BusinessException, DataAccessException {
        boolean wasRegistered = false;

        validateProjectRegistration(
            project,
            activities
        );

        try (Connection connection = DatabaseManager.getConnection()) {
            wasRegistered =
                executeProjectRegistration(
                    project,
                    activities,
                    connection
                );
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al registrar proyecto",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al registrar proyecto",
                sqlException
            );

            throw new BusinessException(
                "No se pudo registrar el proyecto.",
                sqlException
            );
        }

        return wasRegistered;
    }

    private void validateProjectRegistration(
            ProjectDTO project,
            List<ProjectActivityDTO> activities
    ) throws BusinessException, DataAccessException {
        try {
            ProjectValidator.validateForCreate(project);
            ProjectActivityValidator.validateActivityList(activities);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    private boolean executeProjectRegistration(
            ProjectDTO project,
            List<ProjectActivityDTO> activities,
            Connection connection
    ) throws BusinessException, DataAccessException, SQLException {
        boolean wasRegistered = false;

        connection.setAutoCommit(false);

        try {
            int projectId =
                projectDAO.createAndReturnId(
                    project,
                    connection
                );

            registerActivities(
                projectId,
                activities,
                connection
            );

            connection.commit();

            LOGGER.info(
                "Proyecto registrado correctamente con id {}",
                projectId
            );

            wasRegistered = true;
        } catch (BusinessException businessException) {
            rollbackTransaction(connection);

            LOGGER.error(
                "Error de negocio al registrar proyecto",
                businessException
            );

            throw businessException;
        } catch (SQLException sqlException) {
            rollbackTransaction(connection);

            LOGGER.error(
                "Error SQL durante la transacción de registro de proyecto",
                sqlException
            );

            throw sqlException;
        }

        return wasRegistered;
    }

    private void registerActivities(
            Integer projectId,
            List<ProjectActivityDTO> activities,
            Connection connection
    ) throws BusinessException, DataAccessException {
        for (ProjectActivityDTO activity : activities) {
            activity.setProjectId(projectId);

            activityDAO.create(
                activity,
                connection
            );
        }
    }

    private void rollbackTransaction(Connection connection)
            throws BusinessException, DataAccessException {
        try {
            connection.rollback();
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error realizando rollback del registro de proyecto",
                sqlException
            );

            throw new BusinessException("No se pudo revertir el registro del proyecto.", sqlException);
        }
    }
}