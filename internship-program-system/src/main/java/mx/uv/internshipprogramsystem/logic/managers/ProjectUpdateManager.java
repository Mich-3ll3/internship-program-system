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
import mx.uv.internshipprogramsystem.logic.dao.ProjectScheduleDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.ProjectActivityValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProjectScheduleValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProjectValidator;

public class ProjectUpdateManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectUpdateManager.class);

    private final ProjectDAO projectDAO;
    private final ProjectActivityDAO activityDAO;
    private final ProjectScheduleDAO scheduleDAO;

    public ProjectUpdateManager() {
        projectDAO = new ProjectDAO();
        activityDAO = new ProjectActivityDAO();
        scheduleDAO = new ProjectScheduleDAO();
    }

    public boolean updateProject(
            ProjectDTO project,
            List<ProjectActivityDTO> activities,
            List<ProjectScheduleDTO> schedules
    ) throws BusinessException, DataAccessException {
        boolean wasUpdated = false;

        validateUpdate(project, activities, schedules);

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                projectDAO.update(project, connection);

                activityDAO.deleteByProjectId(
                    project.getId(),
                    connection
                );

                scheduleDAO.deleteByProjectId(
                    project.getId(),
                    connection
                );

                registerActivities(
                    project.getId(),
                    activities,
                    connection
                );

                registerSchedules(
                    project.getId(),
                    schedules,
                    connection
                );

                connection.commit();
                wasUpdated = true;
            } catch (BusinessException | SQLException exception) {
                rollback(connection);

                throw new BusinessException(
                    "No se pudo actualizar el proyecto.",
                    exception
                );
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al actualizar proyecto",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al actualizar proyecto",
                sqlException
            );

            throw new BusinessException(
                "No se pudo actualizar el proyecto.",
                sqlException
            );
        }

        return wasUpdated;
    }

    private void validateUpdate(
            ProjectDTO project,
            List<ProjectActivityDTO> activities,
            List<ProjectScheduleDTO> schedules
    ) throws BusinessException, DataAccessException {
        try {
            ProjectValidator.validateForUpdate(project);
            ProjectActivityValidator.validateActivityList(activities);
            ProjectScheduleValidator.validateScheduleList(schedules);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    private void registerActivities(
            Integer projectId,
            List<ProjectActivityDTO> activities,
            Connection connection
    ) throws BusinessException, DataAccessException {
        for (ProjectActivityDTO activity : activities) {
            activity.setProjectId(projectId);
            activityDAO.create(activity, connection);
        }
    }

    private void registerSchedules(
            Integer projectId,
            List<ProjectScheduleDTO> schedules,
            Connection connection
    ) throws BusinessException, DataAccessException {
        for (ProjectScheduleDTO schedule : schedules) {
            schedule.setProjectId(projectId);
            scheduleDAO.create(schedule, connection);
        }
    }

    private void rollback(Connection connection)
            throws BusinessException, DataAccessException {
        try {
            connection.rollback();
        } catch (SQLException sqlException) {
            throw new BusinessException(
                "No se pudo revertir la actualización del proyecto.",
                sqlException
            );
        }
    }

    public boolean updateProjectOnly(
            ProjectDTO project,
            List<ProjectScheduleDTO> schedules
    ) throws BusinessException, DataAccessException {
        boolean wasUpdated = false;

        try {
            ProjectValidator.validateForUpdate(project);
            ProjectScheduleValidator.validateScheduleList(schedules);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                projectDAO.update(project, connection);

                scheduleDAO.deleteByProjectId(
                    project.getId(),
                    connection
                );

                registerSchedules(
                    project.getId(),
                    schedules,
                    connection
                );

                connection.commit();
                wasUpdated = true;
            } catch (BusinessException | SQLException exception) {
                rollback(connection);

                throw new BusinessException(
                    "No se pudo actualizar el proyecto.",
                    exception
                );
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al actualizar proyecto",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al actualizar proyecto",
                sqlException
            );

            throw new BusinessException(
                "No se pudo actualizar el proyecto.",
                sqlException
            );
        }

        return wasUpdated;
    }
}