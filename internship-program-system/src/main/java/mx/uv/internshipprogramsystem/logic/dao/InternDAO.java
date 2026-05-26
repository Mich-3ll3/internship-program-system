package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IInternDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

public class InternDAO implements IInternDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(InternDAO.class);

    private static final String INSERT_INTERN_QUERY =
        "INSERT INTO ESTUDIANTE (matricula, usuario_id) "
        + "VALUES (?, ?)";

    private static final String UPDATE_INTERN_QUERY =
        "UPDATE ESTUDIANTE SET usuario_id = ? "
        + "WHERE matricula = ?";

    private static final String SELECT_INTERN_BY_ENROLLMENT_QUERY =
        "SELECT e.matricula, ee.NRC, u.id, u.correo_institucional, "
        + "u.nombre, u.apellido_paterno, u.apellido_materno, u.activo "
        + "FROM ESTUDIANTE e "
        + "JOIN USUARIO u ON e.usuario_id = u.id "
        + "LEFT JOIN EXPERIENCIA_ESTUDIANTES ee "
        + "ON e.usuario_id = ee.estudiante_id "
        + "WHERE e.matricula = ?";

    private static final String SELECT_ALL_INTERNS_QUERY =
        "SELECT e.matricula, ee.NRC, u.id, u.correo_institucional, "
        + "u.nombre, u.apellido_paterno, u.apellido_materno, u.activo "
        + "FROM ESTUDIANTE e "
        + "JOIN USUARIO u ON e.usuario_id = u.id "
        + "LEFT JOIN EXPERIENCIA_ESTUDIANTES ee "
        + "ON e.usuario_id = ee.estudiante_id";

    private static final String SELECT_COUNT_INTERNS_QUERY =
        "SELECT COUNT(*) AS total FROM ESTUDIANTE";

    @Override
    public boolean create(InternDTO intern, Connection connection) throws BusinessException {
        InputValidator.validateNotNull(connection,"La conexión no puede ser nula.");
        InternValidator internValidator = new InternValidator();
        internValidator.validateInternForCreation(intern);

        boolean wasCreated;

        try (PreparedStatement insertInternStatement =
            connection.prepareStatement(INSERT_INTERN_QUERY)) {
            insertInternStatement.setString(1, intern.getEnrollmentNumber());
            insertInternStatement.setInt(2,intern.getId());

            wasCreated = insertInternStatement.executeUpdate() > 0;
            
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad: matrícula duplicada", integrityException);
            throw new BusinessException("La matrícula ya existe.",integrityException);
        } catch (SQLException insertException) {
            LOGGER.error("Error SQL al insertar estudiante", insertException);
            throw new BusinessException(
                "Error al insertar el estudiante en la base de datos.",
                insertException
            );
        }
        return wasCreated;
    }

    @Override
    public boolean update(InternDTO intern) throws BusinessException {
        InputValidator.validateNotNull(
            intern,
            "InternDTO no puede ser nulo."
        );
        validateIntern(intern);

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateInternStatement =
                 connection.prepareStatement(UPDATE_INTERN_QUERY)) {
            updateInternStatement.setInt(1, intern.getId());
            updateInternStatement.setString(
                2,
                intern.getEnrollmentNumber()
            );

            wasUpdated = updateInternStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error(
                "Violación de integridad al actualizar estudiante",
                integrityException
            );

            throw new BusinessException(
                "El usuario asociado no existe o la matrícula es inválida.",
                integrityException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al actualizar estudiante con matrícula {}",
                intern.getEnrollmentNumber(),
                sqlException
            );

            throw new BusinessException(
                "Error actualizando estudiante con matrícula "
                    + intern.getEnrollmentNumber(),
                sqlException
            );
        }

        return wasUpdated;
    }

    @Override
    public Optional<InternDTO> findByEnrollmentNumber(
            String enrollmentNumber
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            enrollmentNumber,
            "La matrícula no puede estar vacía."
        );

        Optional<InternDTO> intern;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectInternStatement =
                 connection.prepareStatement(
                     SELECT_INTERN_BY_ENROLLMENT_QUERY
                 )) {
            selectInternStatement.setString(1, enrollmentNumber);

            try (ResultSet resultSet =
                    selectInternStatement.executeQuery()) {
                intern = buildOptionalIntern(resultSet);
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException selectException) {
            LOGGER.error(
                "Error SQL al buscar estudiante",
                selectException
            );

            throw new BusinessException(
                "Error buscando estudiante con matrícula "
                    + enrollmentNumber,
                selectException
            );
        }

        return intern;
    }

    @Override
    public List<InternDTO> findAll() throws BusinessException {
        List<InternDTO> interns = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllInternsStatement =
                 connection.prepareStatement(SELECT_ALL_INTERNS_QUERY);
             ResultSet resultSet =
                 selectAllInternsStatement.executeQuery()) {
            while (resultSet.next()) {
                interns.add(buildIntern(resultSet));
            }
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
                "Error SQL al obtener la lista de estudiantes",
                sqlException
            );

            throw new BusinessException(
                "Error obteniendo la lista de estudiantes.",
                sqlException
            );
        }

        return List.copyOf(interns);
    }

    @Override
    public int countAll() throws BusinessException {
        int totalInterns = 0;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectCountInternsStatement =
                 connection.prepareStatement(SELECT_COUNT_INTERNS_QUERY);
             ResultSet resultSet =
                 selectCountInternsStatement.executeQuery()) {
            if (resultSet.next()) {
                totalInterns = resultSet.getInt("total");
            }
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
                "Error SQL al contar estudiantes",
                sqlException
            );

            throw new BusinessException(
                "Error al obtener el total de estudiantes.",
                sqlException
            );
        }

        return totalInterns;
    }

    private void validateIntern(InternDTO intern) throws BusinessException {
        InternValidator validator = new InternValidator();

        validator.validateEnrollmentNumber(
            intern.getEnrollmentNumber()
        );
    }

    private Optional<InternDTO> buildOptionalIntern(ResultSet resultSet)
            throws SQLException {
        Optional<InternDTO> intern;

        if (resultSet.next()) {
            intern = Optional.of(buildIntern(resultSet));
        } else {
            intern = Optional.empty();
        }

        return intern;
    }

    private InternDTO buildIntern(ResultSet resultSet) throws SQLException {
        InternDTO intern = new InternDTO();

        intern.setEnrollmentNumber(resultSet.getString("matricula"));
        intern.setId(resultSet.getInt("id"));
        intern.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );
        intern.setName(resultSet.getString("nombre"));
        intern.setFirstSurname(resultSet.getString("apellido_paterno"));
        intern.setSecondSurname(resultSet.getString("apellido_materno"));
        intern.setIsActive(resultSet.getBoolean("activo"));
        intern.setNrc(resultSet.getString("NRC"));

        return intern;
    }
}