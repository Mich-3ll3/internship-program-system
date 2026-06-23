package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternStatus;
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

    private static final String SELECT_ASSIGNMENTS_BY_NRC_QUERY =
        "SELECT ee.NRC, ee.estudiante_id, ee.fecha_asignacion, "
        + "ee.cuenta_oportunidad, ee.numero_oportunidad, ee.estado, "
        + "e.matricula, u.correo_institucional, "
        + "CONCAT(u.nombre, ' ', u.apellido_paterno, ' ', "
        + "COALESCE(u.apellido_materno, '')) AS nombre_estudiante "
        + "FROM EXPERIENCIA_ESTUDIANTES ee "
        + "JOIN ESTUDIANTE e ON ee.estudiante_id = e.usuario_id "
        + "JOIN USUARIO u ON e.usuario_id = u.id "
        + "WHERE ee.NRC = ? "
        + "ORDER BY u.apellido_paterno, u.apellido_materno, u.nombre";

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

    private static final String CLOSE_ACTIVE_ASSIGNMENT_QUERY =
        "UPDATE EXPERIENCIA_ESTUDIANTES "
        + "SET estado = ? "
        + "WHERE estudiante_id = ? "
        + "AND estado = 'ACTIVA'";

    @Override
    public boolean create(
            EducationalExperienceInternDTO assignment
    ) throws BusinessException, DataAccessException {
        boolean wasCreated;

        try (Connection connection = DatabaseManager.getConnection();
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
    ) throws BusinessException, DataAccessException {
        boolean existsAssignment;

        try (Connection connection = DatabaseManager.getConnection();
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
    public List<EducationalExperienceInternDTO> findByNrc(
            String nrc
    ) throws BusinessException, DataAccessException {
        List<EducationalExperienceInternDTO> assignments =
            new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        SELECT_ASSIGNMENTS_BY_NRC_QUERY
                    )) {
            selectStatement.setString(
                1,
                nrc
            );

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                while (resultSet.next()) {
                    assignments.add(
                        buildAssignmentWithInternData(
                            resultSet
                        )
                    );
                }
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexion con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al consultar estudiantes por NRC",
                sqlException
            );

            throw new BusinessException(
                "Error al consultar estudiantes inscritos al NRC.",
                sqlException
            );
        }

        return List.copyOf(assignments);
    }

    @Override
    public boolean existsActiveAssignmentByInternId(
            int internId
    ) throws BusinessException, DataAccessException {
        boolean existsActiveAssignment;

        try (Connection connection = DatabaseManager.getConnection();
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
    ) throws BusinessException, DataAccessException {
        boolean existsActiveExperience;

        try (Connection connection = DatabaseManager.getConnection();
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
    ) throws BusinessException, DataAccessException {
        int opportunityCount;

        try (Connection connection = DatabaseManager.getConnection();
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

    @Override
    public boolean closeActiveEducationalExperience(
            int internId,
            EducationalExperienceInternStatus status
    ) throws BusinessException, DataAccessException {
        boolean wasClosed;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement updateStatement =
                    connection.prepareStatement(
                        CLOSE_ACTIVE_ASSIGNMENT_QUERY
                    )) {
            updateStatement.setString(
                1,
                status.name()
            );
            updateStatement.setInt(
                2,
                internId
            );

            wasClosed =
                updateStatement.executeUpdate() > 0;

            LOGGER.info(
                "Inscripcion activa del estudiante cerrada con estado {}.",
                status
            );
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexion con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al cerrar experiencia activa del estudiante",
                sqlException
            );

            throw new BusinessException(
                "Error al cerrar la experiencia educativa activa del estudiante.",
                sqlException
            );
        }

        return wasClosed;
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

    private EducationalExperienceInternDTO buildAssignmentWithInternData(
            ResultSet resultSet
    ) throws SQLException {
        Date assignmentDate =
            resultSet.getDate("fecha_asignacion");

        EducationalExperienceInternDTO assignment =
            new EducationalExperienceInternDTO(
                resultSet.getString("NRC"),
                resultSet.getInt("estudiante_id"),
                assignmentDate != null ? assignmentDate.toLocalDate() : null,
                resultSet.getBoolean("cuenta_oportunidad"),
                resultSet.getInt("numero_oportunidad"),
                EducationalExperienceInternStatus.valueOf(
                    resultSet.getString("estado")
                )
            );

        assignment.setEnrollmentNumber(
            resultSet.getString("matricula")
        );
        assignment.setInternName(
            resultSet.getString("nombre_estudiante")
        );
        assignment.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );

        return assignment;
    }
}