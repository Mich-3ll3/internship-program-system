package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces
        .IEducationalExperienceInternDAO;

public class EducationalExperienceInternDAO implements IEducationalExperienceInternDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceInternDAO.class
        );

    private static final String INSERT_ASSIGNMENT_QUERY =
        "INSERT INTO EXPERIENCIA_ESTUDIANTES "
        + "(NRC, estudiante_id, fecha_asignacion, cuenta_oportunidad, "
        + "numero_oportunidad, estado) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String EXISTS_ASSIGNMENT_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_ESTUDIANTES "
        + "WHERE NRC = ? "
        + "AND estudiante_id = ?";

    private static final String EXISTS_ACTIVE_ASSIGNMENT_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_ESTUDIANTES "
        + "WHERE estudiante_id = ? "
        + "AND estado = 'ACTIVA'";

    private static final String EXISTS_ACTIVE_EXPERIENCE_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_EDUCATIVA "
        + "WHERE NRC = ? "
        + "AND activa = TRUE";

    private static final String COUNT_VALID_OPPORTUNITIES_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_ESTUDIANTES "
        + "WHERE estudiante_id = ? "
        + "AND cuenta_oportunidad = TRUE";

    @Override
    public boolean create(
            EducationalExperienceInternDTO assignment
    ) throws BusinessException {
        boolean wasCreated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertStatement =
                    connection.prepareStatement(
                        INSERT_ASSIGNMENT_QUERY
                    )) {
            setFullAssignmentParameters(
                insertStatement,
                assignment
            );

            wasCreated =
                insertStatement.executeUpdate() > 0;

            LOGGER.info(
                "Estudiante asignado correctamente a experiencia educativa."
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
        } catch (
                SQLIntegrityConstraintViolationException integrityException
        ) {
            LOGGER.error(
                "Violación de integridad al asignar experiencia educativa",
                integrityException
            );

            throw new BusinessException(
                "La experiencia educativa o el estudiante no son válidos.",
                integrityException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al asignar experiencia educativa",
                sqlException
            );

            throw new BusinessException(
                "Error al asignar experiencia educativa al estudiante.",
                sqlException
            );
        }

        return wasCreated;
    }

    @Override
    public boolean existsAssignment(
            EducationalExperienceInternDTO assignment
    ) throws BusinessException {
        boolean existsAssignment;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        EXISTS_ASSIGNMENT_QUERY
                    )) {
            setBasicAssignmentParameters(
                selectStatement,
                assignment
            );

            existsAssignment =
                existsByPreparedStatement(
                    selectStatement
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
                "Error SQL al verificar asignación existente",
                sqlException
            );

            throw new BusinessException(
                "Error al verificar si la asignación ya existe.",
                sqlException
            );
        }

        return existsAssignment;
    }

    @Override
    public boolean existsActiveAssignmentByInternId(
            int internId
    ) throws BusinessException {
        boolean existsActiveAssignment;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        EXISTS_ACTIVE_ASSIGNMENT_QUERY
                    )) {
            selectStatement.setInt(
                1,
                internId
            );

            existsActiveAssignment =
                existsByPreparedStatement(
                    selectStatement
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
                "Error SQL al verificar experiencia activa del estudiante",
                sqlException
            );

            throw new BusinessException(
                "Error al verificar la experiencia activa del estudiante.",
                sqlException
            );
        }

        return existsActiveAssignment;
    }

    @Override
    public boolean existsActiveEducationalExperienceByNrc(
            String nrc
    ) throws BusinessException {
        boolean existsActiveExperience;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        EXISTS_ACTIVE_EXPERIENCE_QUERY
                    )) {
            selectStatement.setString(
                1,
                nrc
            );

            existsActiveExperience =
                existsByPreparedStatement(
                    selectStatement
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
                "Error SQL al verificar experiencia educativa activa",
                sqlException
            );

            throw new BusinessException(
                "Error al verificar la experiencia educativa.",
                sqlException
            );
        }

        return existsActiveExperience;
    }

    public int countValidOpportunitiesByInternId(
            int internId
    ) throws BusinessException {
        int opportunityCount;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        COUNT_VALID_OPPORTUNITIES_QUERY
                    )) {
            selectStatement.setInt(
                1,
                internId
            );

            opportunityCount =
                countByPreparedStatement(
                    selectStatement
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
                "Error SQL al contar oportunidades del estudiante",
                sqlException
            );

            throw new BusinessException(
                "Error al contar oportunidades del estudiante.",
                sqlException
            );
        }

        return opportunityCount;
    }

    private void setBasicAssignmentParameters(
            PreparedStatement statement,
            EducationalExperienceInternDTO assignment
    ) throws SQLException {
        statement.setString(
            1,
            assignment.getNrc()
        );

        statement.setInt(
            2,
            assignment.getInternId()
        );
    }

    private void setFullAssignmentParameters(
            PreparedStatement statement,
            EducationalExperienceInternDTO assignment
    ) throws SQLException {
        setBasicAssignmentParameters(
            statement,
            assignment
        );

        statement.setDate(
            3,
            Date.valueOf(
                assignment.getAssignmentDate()
            )
        );

        statement.setBoolean(
            4,
            assignment.getCountsOpportunity()
        );

        statement.setInt(
            5,
            assignment.getOpportunityNumber()
        );

        statement.setString(
            6,
            assignment.getStatus().name()
        );
    }

    private boolean existsByPreparedStatement(
            PreparedStatement statement
    ) throws SQLException {
        boolean exists;

        int total =
            countByPreparedStatement(
                statement
            );

        exists =
            total > 0;

        return exists;
    }

    private int countByPreparedStatement(
            PreparedStatement statement
    ) throws SQLException {
        int total = 0;

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                total =
                    resultSet.getInt(
                        "total"
                    );
            }
        }

        return total;
    }
}