package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
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
        + "(nombre, mes, semana_inicio, semana_fin, proyecto_id) "
        + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_BY_PROJECT_QUERY =
        "SELECT id, nombre, mes, semana_inicio, "
        + "semana_fin, proyecto_id "
        + "FROM ACTIVIDADES_PLAN "
        + "WHERE proyecto_id = ?";

    private static final String DELETE_BY_PROJECT_QUERY =
        "DELETE FROM ACTIVIDADES_PLAN "
        + "WHERE proyecto_id = ?";

    @Override
    public boolean create(
            ProjectActivityDTO activity,
            Connection connection
    ) throws BusinessException {

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
    ) throws BusinessException {

        InputValidator.validateNotNull(
            projectId,
            "El id del proyecto no puede ser nulo."
        );

        List<ProjectActivityDTO> activities =
            new ArrayList<>();

        try (Connection connection =
                DataBaseManager.getConnection();

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
    ) throws BusinessException {

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
                resultSet.getInt("proyecto_id")
            );

        return activity;
    }
}
