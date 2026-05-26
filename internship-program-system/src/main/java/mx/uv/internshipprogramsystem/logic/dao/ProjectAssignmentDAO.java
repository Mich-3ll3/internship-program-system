package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectAssignmentDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectAssignmentDAO
        implements IProjectAssignmentDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectAssignmentDAO.class
        );

    private static final String INSERT_ASSIGNMENT_QUERY =
        "INSERT INTO ASIGNACION_PROYECTO "
        + "(estudiante_id, proyecto_id, profesor_id, "
        + "fecha_asignacion) "
        + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_ASSIGNMENT_BY_ID_QUERY =
        "SELECT id, estudiante_id, proyecto_id, "
        + "profesor_id, fecha_asignacion "
        + "FROM ASIGNACION_PROYECTO "
        + "WHERE id = ?";

    private static final String DELETE_ASSIGNMENT_QUERY =
        "DELETE FROM ASIGNACION_PROYECTO "
        + "WHERE id = ?";

    @Override
    public boolean insert(
            ProjectAssignmentDTO assignment
    ) throws BusinessException {
        InputValidator.validateNotNull(
            assignment,
            "ProjectAssignmentDTO no puede ser nulo."
        );

        boolean wasInserted;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement insertAssignmentStatement =
                 connection.prepareStatement(
                     INSERT_ASSIGNMENT_QUERY
                 )) {
            insertAssignmentStatement.setInt(
                1,
                assignment.getStudentId()
            );

            insertAssignmentStatement.setInt(
                2,
                assignment.getProjectId()
            );

            insertAssignmentStatement.setInt(
                3,
                assignment.getProfessorId()
            );

            insertAssignmentStatement.setDate(
                4,
                Date.valueOf(
                    assignment.getAssignmentDate()
                )
            );

            LOGGER.debug(
                "Valores recibidos → estudiante: {}, "
                    + "proyecto: {}, profesor: {}, fecha: {}",
                assignment.getStudentId(),
                assignment.getProjectId(),
                assignment.getProfessorId(),
                assignment.getAssignmentDate()
            );

            int affectedRows =
                insertAssignmentStatement.executeUpdate();

            wasInserted = affectedRows > 0;

            LOGGER.info(
                "Asignación insertada: estudiante {} "
                    + "→ proyecto {} con profesor {}",
                assignment.getStudentId(),
                assignment.getProjectId(),
                assignment.getProfessorId()
            );
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error insertando asignación estudiante {} "
                    + "→ proyecto {} con profesor {}",
                assignment.getStudentId(),
                assignment.getProjectId(),
                assignment.getProfessorId(),
                sqlException
            );

            throw new BusinessException(
                "Error insertando asignación de proyecto.",
                sqlException
            );
        }

        return wasInserted;
    }

    @Override
    public Optional<ProjectAssignmentDTO> findById(
            int id
    ) throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id de la asignación debe ser positivo."
        );

        Optional<ProjectAssignmentDTO> assignment;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement selectAssignmentStatement =
                 connection.prepareStatement(
                     SELECT_ASSIGNMENT_BY_ID_QUERY
                 )) {
            selectAssignmentStatement.setInt(1, id);

            try (ResultSet resultSetAssignment =
                    selectAssignmentStatement.executeQuery()) {
                assignment =
                    buildOptionalAssignment(
                        resultSetAssignment,
                        id
                    );
            }
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error buscando asignación con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error buscando asignación con id "
                    + id,
                sqlException
            );
        }

        return assignment;
    }

    @Override
    public boolean delete(
            int id
    ) throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id de la asignación debe ser positivo."
        );

        boolean wasDeleted;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement deleteAssignmentStatement =
                 connection.prepareStatement(
                     DELETE_ASSIGNMENT_QUERY
                 )) {
            deleteAssignmentStatement.setInt(1, id);

            int affectedRows =
                deleteAssignmentStatement.executeUpdate();

            wasDeleted = affectedRows > 0;

            if (wasDeleted) {
                LOGGER.info(
                    "Asignación con id {} eliminada correctamente",
                    id
                );
            } else {
                LOGGER.warn(
                    "No se eliminó asignación con id {}",
                    id
                );
            }

            LOGGER.warn("No se eliminó asignación con id {}", id);
            return false;
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error eliminando asignación con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error eliminando asignación con id "
                    + id,
                sqlException
            );
        }

        return wasDeleted;
    }

    private Optional<ProjectAssignmentDTO>
            buildOptionalAssignment(
                ResultSet resultSetAssignment,
                int id
    ) throws SQLException {
        Optional<ProjectAssignmentDTO> assignment;

        if (resultSetAssignment.next()) {
            LOGGER.info(
                "Asignación encontrada con id {}",
                id
            );

            assignment =
                Optional.of(
                    buildAssignment(
                        resultSetAssignment
                    )
                );
        } else {
            LOGGER.warn(
                "No se encontró asignación con id {}",
                id
            );

            assignment = Optional.empty();
        }

        return assignment;
    }

    private ProjectAssignmentDTO buildAssignment(
            ResultSet resultSetAssignment
    ) throws SQLException {
        ProjectAssignmentDTO assignment =
            new ProjectAssignmentDTO(
                resultSetAssignment.getInt("id"),
                resultSetAssignment.getInt(
                    "estudiante_id"
                ),
                resultSetAssignment.getInt(
                    "proyecto_id"
                ),
                resultSetAssignment.getInt(
                    "profesor_id"
                ),
                resultSetAssignment.getDate(
                    "fecha_asignacion"
                ).toLocalDate()
            );

        return assignment;
    }
}
