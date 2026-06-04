package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectScheduleDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectScheduleDAO implements IProjectScheduleDAO {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectScheduleDAO.class
        );

    private static final String INSERT_SCHEDULE_QUERY =
        "INSERT INTO HORARIO "
        + "(dia_semana, hora_entrada, "
        + "hora_salida, proyecto_id) "
        + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_BY_PROJECT_QUERY =
        "SELECT id, dia_semana, hora_entrada, "
        + "hora_salida, proyecto_id "
        + "FROM HORARIO "
        + "WHERE proyecto_id = ?";

    private static final String DELETE_BY_PROJECT_QUERY =
        "DELETE FROM HORARIO "
        + "WHERE proyecto_id = ?";

    @Override
    public boolean create(
            ProjectScheduleDTO schedule,
            Connection connection
    ) throws BusinessException {

        InputValidator.validateNotNull(
            schedule,
            "El horario no puede ser nulo."
        );

        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );

        boolean wasCreated;

        try (PreparedStatement statement =
                connection.prepareStatement(
                    INSERT_SCHEDULE_QUERY
                )) {

            statement.setString(
                1,
                schedule.getWeekDay()
            );

            statement.setTime(
                2,
                Time.valueOf(
                    schedule.getEntryTime()
                )
            );

            statement.setTime(
                3,
                Time.valueOf(
                    schedule.getExitTime()
                )
            );

            statement.setInt(
                4,
                schedule.getProjectId()
            );

            wasCreated =
                statement.executeUpdate() > 0;

        } catch (SQLException exception) {

            LOGGER.error(
                "Error registrando horario.",
                exception
            );

            throw new BusinessException(
                "Error registrando horario.",
                exception
            );
        }

        return wasCreated;
    }

    @Override
    public List<ProjectScheduleDTO> findByProjectId(
            Integer projectId
    ) throws BusinessException {

        InputValidator.validateNotNull(
            projectId,
            "El id del proyecto no puede ser nulo."
        );

        List<ProjectScheduleDTO> schedules =
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

                    schedules.add(
                        buildSchedule(
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
                "Error consultando horarios.",
                exception
            );

            throw new BusinessException(
                "Error consultando horarios.",
                exception
            );
        }

        return List.copyOf(
            schedules
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
                "Error eliminando horarios.",
                exception
            );

            throw new BusinessException(
                "Error eliminando horarios.",
                exception
            );
        }

        return wasDeleted;
    }

    private ProjectScheduleDTO buildSchedule(
            ResultSet resultSet
    ) throws SQLException {

        ProjectScheduleDTO schedule =
            new ProjectScheduleDTO(
                resultSet.getInt("id"),
                resultSet.getString("dia_semana"),
                resultSet.getTime(
                    "hora_entrada"
                ).toLocalTime(),
                resultSet.getTime(
                    "hora_salida"
                ).toLocalTime(),
                resultSet.getInt("proyecto_id")
            );

        return schedule;
    }
}