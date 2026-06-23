package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectActivityDAO implements IProjectActivityDAO {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectActivityDAO.class
        );

    private static final String INSERT_ACTIVITY_QUERY =
        "INSERT INTO ACTIVIDADES_PLAN "
        + "(nombre, mes, semana_inicio, semana_fin, horas_planeadas, proyecto_id) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_PROJECT_QUERY =
        "SELECT id, nombre, mes, semana_inicio, "
        + "semana_fin, horas_planeadas, proyecto_id "
        + "FROM ACTIVIDADES_PLAN "
        + "WHERE proyecto_id = ? AND nombre NOT LIKE '__DELETED__%'";

    private static final String SELECT_ALL_PHYSICAL_BY_PROJECT_QUERY =
        "SELECT id, nombre, mes, semana_inicio, "
        + "semana_fin, horas_planeadas, proyecto_id "
        + "FROM ACTIVIDADES_PLAN "
        + "WHERE proyecto_id = ?";

    private static final String DELETE_BY_PROJECT_QUERY =
        "DELETE FROM ACTIVIDADES_PLAN "
        + "WHERE proyecto_id = ?";

    private static final String SELECT_ALL_ACTIVITIES_QUERY =
        "SELECT id, nombre, mes, semana_inicio, "
        + "semana_fin, horas_planeadas, proyecto_id "
        + "FROM ACTIVIDADES_PLAN WHERE nombre NOT LIKE '__DELETED__%'";

    private static final String UPDATE_ACTIVITY_QUERY =
        "UPDATE ACTIVIDADES_PLAN SET nombre = ?, "
        + "mes = ?, semana_inicio = ?, semana_fin = ?, "
        + "horas_planeadas = ?, proyecto_id = ? WHERE id = ?";

    private static final String DELETE_ACTIVITY_QUERY =
        "DELETE FROM ACTIVIDADES_PLAN "
        + "WHERE id = ?";

    @Override
    public boolean create(
            ProjectActivityDTO activity,
            Connection connection
    ) throws BusinessException, DataAccessException {

        InputValidator.validateNotNull(
            activity,
            "La actividad no puede ser nula."
        );

        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );

        boolean wasCreated;

        try (PreparedStatement statement =
                connection.prepareStatement(
                    INSERT_ACTIVITY_QUERY
                )) {

            statement.setString(
                1,
                activity.getName()
            );

            statement.setString(
                2,
                activity.getMonth()
            );

            statement.setInt(
                3,
                activity.getStartWeek()
            );

            statement.setInt(
                4,
                activity.getEndWeek()
            );

            statement.setInt(
                5,
                activity.getPlannedHours()
            );

            statement.setInt(
                6,
                activity.getProjectId()
            );

            wasCreated =
                statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            LOGGER.error(
                "Error registrando actividad.",
                exception
            );

            throw new BusinessException(
                "Error registrando actividad.",
                exception
            );
        }

        return wasCreated;
    }

    @Override
    public List<ProjectActivityDTO> findByProjectId(
            Integer projectId
    ) throws BusinessException, DataAccessException {

        InputValidator.validateNotNull(
            projectId,
            "El id del proyecto no puede ser nulo."
        );

        List<ProjectActivityDTO> activities =
            new ArrayList<>();

        try (Connection connection =
                DatabaseManager.getConnection();

             PreparedStatement statement =
                connection.prepareStatement(
                    SELECT_BY_PROJECT_QUERY
                )) {

            statement.setInt(
                1,
                projectId
            );

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    activities.add(
                        buildActivity(
                            resultSet
                        )
                    );
                }
            }

        } catch (
            SQLTransientConnectionException exception
        ) {

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                exception
            );

        } catch (SQLException exception) {

            LOGGER.error(
                "Error consultando actividades.",
                exception
            );

            throw new BusinessException(
                "Error consultando actividades.",
                exception
            );
        }

        return List.copyOf(
            activities
        );
    }

    @Override
    public boolean deleteByProjectId(
            Integer projectId,
            Connection connection
    ) throws BusinessException, DataAccessException {

        InputValidator.validateNotNull(
            projectId,
            "El id del proyecto no puede ser nulo."
        );

        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );

        boolean wasDeleted;

        try (PreparedStatement statement =
                connection.prepareStatement(
                    DELETE_BY_PROJECT_QUERY
                )) {

            statement.setInt(
                1,
                projectId
            );

            wasDeleted =
                statement.executeUpdate() >= 0;

        } catch (SQLException exception) {

            LOGGER.error(
                "Error eliminando actividades.",
                exception
            );

            throw new BusinessException(
                "Error eliminando actividades.",
                exception
            );
        }

        return wasDeleted;
    }

    @Override
    public boolean create(ProjectActivityDTO activity) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(activity, "La actividad no puede ser nula.");
        boolean wasCreated = false;
        try (Connection connection = DatabaseManager.getConnection()) {
            wasCreated = create(activity, connection);
        } catch (SQLTransientConnectionException exception) {
            throw new BusinessException("No se pudo conectar con la base de datos.", exception);
        } catch (SQLException exception) {
            LOGGER.error("Error registrando actividad.", exception);
            throw new BusinessException("Error registrando actividad.", exception);
        }
        return wasCreated;
    }

    public boolean update(
            ProjectActivityDTO activity,
            Connection connection
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            activity,
            "La actividad no puede ser nula."
        );
        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );
        InputValidator.validatePositive(
            activity.getId(),
            "El id de la actividad debe ser positivo."
        );

        boolean wasUpdated;
        try (PreparedStatement statement =
                connection.prepareStatement(
                    UPDATE_ACTIVITY_QUERY
                )) {

            statement.setString(1, activity.getName());
            statement.setString(2, activity.getMonth());
            statement.setInt(3, activity.getStartWeek());
            statement.setInt(4, activity.getEndWeek());
            statement.setInt(5, activity.getPlannedHours());
            statement.setInt(6, activity.getProjectId());
            statement.setInt(7, activity.getId());

            wasUpdated = statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            LOGGER.error(
                "Error actualizando actividad.",
                exception
            );
            throw new BusinessException(
                "Error actualizando actividad.",
                exception
            );
        }
        return wasUpdated;
    }

    @Override
    public boolean update(ProjectActivityDTO activity) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(activity, "La actividad no puede ser nula.");
        InputValidator.validatePositive(activity.getId(), "El id de la actividad debe ser positivo.");

        boolean wasUpdated;
        try (Connection connection = DatabaseManager.getConnection()) {
            wasUpdated = update(activity, connection);
        } catch (SQLTransientConnectionException exception) {
            throw new BusinessException("No se pudo conectar con la base de datos.", exception);
        } catch (SQLException exception) {
            LOGGER.error("Error actualizando actividad.", exception);
            throw new BusinessException("Error actualizando actividad.", exception);
        }

        return wasUpdated;
    }

    @Override
    public boolean delete(int id) throws BusinessException, DataAccessException {
        InputValidator.validatePositive(id, "El id de la actividad debe ser positivo.");

        boolean wasDeleted;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ACTIVITY_QUERY)) {

            statement.setInt(1, id);
            wasDeleted = statement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException exception) {
            throw new BusinessException("No se pudo conectar con la base de datos.", exception);
        } catch (SQLException exception) {
            LOGGER.error("Error deleting activity.", exception);
            throw new BusinessException("Error deleting activity.", exception);
        }

        return wasDeleted;
    }

    @Override
    public List<ProjectActivityDTO> findAll() throws BusinessException, DataAccessException {
        List<ProjectActivityDTO> activities = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ACTIVITIES_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                activities.add(buildActivity(resultSet));
            }
        } catch (SQLTransientConnectionException exception) {
            throw new BusinessException("No se pudo conectar con la base de datos.", exception);
        } catch (SQLException exception) {
            LOGGER.error("Error consultando actividades.", exception);
            throw new BusinessException("Error consultando actividades.", exception);
        }

        return List.copyOf(activities);
    }

    private ProjectActivityDTO buildActivity(
            ResultSet resultSet
    ) throws SQLException {

        ProjectActivityDTO activity =
            new ProjectActivityDTO(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("mes"),
                resultSet.getInt("semana_inicio"),
                resultSet.getInt("semana_fin"),
                resultSet.getInt("horas_planeadas"),
                resultSet.getInt("proyecto_id")
            );

        return activity;
    }

    @Override
    public boolean saveAll(Integer projectId, List<ProjectActivityDTO> activities)
            throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(projectId, "El id del proyecto no puede ser nulo.");
        InputValidator.validateNotNull(activities, "La lista de actividades no puede ser nula.");

        boolean success = false;
        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Fetch existing physical rows (including ones marked as __DELETED__)
                List<ProjectActivityDTO> existingPhysicalRows = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement(
                        SELECT_ALL_PHYSICAL_BY_PROJECT_QUERY)) {
                    statement.setInt(1, projectId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            existingPhysicalRows.add(buildActivity(resultSet));
                        }
                    }
                }

                int nNew = activities.size();
                int nOld = existingPhysicalRows.size();

                for (int i = 0; i < Math.max(nNew, nOld); i++) {
                    if (i < nNew) {
                        ProjectActivityDTO newActivity = activities.get(i);
                        newActivity.setProjectId(projectId);
                        
                        if (i < nOld) {
                            // Update existing row
                            ProjectActivityDTO existingRow = existingPhysicalRows.get(i);
                            newActivity.setId(existingRow.getId());
                            update(newActivity, connection);
                        } else {
                            // Insert new row
                            create(newActivity, connection);
                        }
                    } else {
                        // Mark leftover database row as deleted
                        ProjectActivityDTO existingRow = existingPhysicalRows.get(i);
                        existingRow.setName("__DELETED__" + existingRow.getId());
                        existingRow.setMonth("__DELETED__");
                        existingRow.setStartWeek(0);
                        existingRow.setEndWeek(0);
                        existingRow.setPlannedHours(0);
                        update(existingRow, connection);
                    }
                }

                connection.commit();
                success = true;
            } catch (Exception exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    LOGGER.error("Error al revertir la transacción de guardado de actividades.", rollbackException);
                }
                throw exception;
            }
        } catch (SQLTransientConnectionException exception) {
            throw new BusinessException("No se pudo conectar con la base de datos.", exception);
        } catch (SQLException exception) {
            LOGGER.error("Error de base de datos al guardar actividades del proyecto.", exception);
            throw new DataAccessException("Error de conexión a la base de datos.", exception);
        }
        return success;
    }
}