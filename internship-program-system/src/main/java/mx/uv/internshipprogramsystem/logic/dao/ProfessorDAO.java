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
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProfessorDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class ProfessorDAO implements IProfessorDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProfessorDAO.class);

    private static final String INSERT_PROFESSOR_QUERY =
        "INSERT INTO PROFESOR "
        + "(numero_personal, es_coordinador, usuario_id) "
        + "VALUES (?, ?, ?)";

    private static final String UPDATE_PROFESSOR_QUERY =
        "UPDATE PROFESOR SET numero_personal = ?, "
        + "es_coordinador = ? "
        + "WHERE usuario_id = ?";

    private static final String SELECT_PROFESSORS_BY_NAME_QUERY =
        "SELECT p.numero_personal, p.es_coordinador, u.id, "
        + "u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id "
        + "WHERE CONCAT("
        + "u.nombre, ' ', "
        + "u.apellido_paterno, ' ', "
        + "COALESCE(u.apellido_materno, '')"
        + ") LIKE ?";

    private static final String SELECT_PROFESSOR_BY_STAFF_NUMBER_QUERY =
        "SELECT p.numero_personal, p.es_coordinador, u.id, "
        + "u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, "
        + "u.activo, COUNT(e.profesor_id) AS grupos "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id "
        + "LEFT JOIN EXPERIENCIA_EDUCATIVA e "
        + "ON p.usuario_id = e.profesor_id "
        + "WHERE p.numero_personal = ? "
        + "GROUP BY p.numero_personal, p.es_coordinador, "
        + "u.id, u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo";

    private static final String SELECT_ALL_PROFESSORS_QUERY =
        "SELECT p.numero_personal, p.es_coordinador, u.id, "
        + "u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id";

    private static final String SELECT_ALL_PROFESSORS_NAME_QUERY =
        "SELECT u.nombre "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id";

    private static final String SELECT_COORDINATOR_QUERY =
        "SELECT p.numero_personal, p.es_coordinador, u.* "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id "
        + "WHERE p.es_coordinador = true";

    private static final String SELECT_COUNT_PROFESSORS_QUERY =
        "SELECT COUNT(*) AS total FROM PROFESOR";

    @Override
    public boolean create(ProfessorDTO professor, Connection connection) throws BusinessException {
        InputValidator.validateNotNull(connection, "La conexión no puede ser nula.");
        ProfessorValidator professorValidator = new ProfessorValidator();
        professorValidator.validateProfessorForCreation(professor);

        boolean wasCreated;

        try (PreparedStatement insertProfessorStatement =
                connection.prepareStatement(
                    INSERT_PROFESSOR_QUERY
                )) {
            insertProfessorStatement.setString(1, professor.getStaffNumber());
            insertProfessorStatement.setBoolean(2, professor.getIsCoordinator());
            insertProfessorStatement.setInt(3, professor.getId());

            wasCreated = insertProfessorStatement.executeUpdate() > 0;
        } catch (
                SQLIntegrityConstraintViolationException integrityException
        ) {
            LOGGER.error(
                "Violación de integridad: número de personal duplicado",
                integrityException
            );

            throw new BusinessException(
                "El número de personal ya existe.",
                integrityException
            );
        } catch (SQLException insertException) {
            LOGGER.error(
                "Error SQL al insertar profesor",
                insertException
            );

            throw new BusinessException(
                "Error al insertar el profesor en la base de datos.",
                insertException
            );
        }

        return wasCreated;
    }

    @Override
    public boolean update(
            ProfessorDTO professor
    ) throws BusinessException {
        InputValidator.validateNotNull(
            professor,
            "ProfessorDTO no puede ser nulo."
        );

        validateProfessor(professor);

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateProfessorStatement =
                connection.prepareStatement(
                    UPDATE_PROFESSOR_QUERY
                    )) {
            updateProfessorStatement.setString(
                1,
                professor.getStaffNumber()
            );
            updateProfessorStatement.setBoolean(
                2,
                professor.getIsCoordinator()
            );
            updateProfessorStatement.setInt(
                3,
                professor.getId()
            );

            wasUpdated =
                updateProfessorStatement.executeUpdate() > 0;
        } catch (
                SQLTransientConnectionException connectionException
        ) {
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
                "Violación de integridad al actualizar profesor",
                integrityException
            );

            throw new BusinessException(
                "El número de personal ya existe o "
                + "el usuario asociado no es válido.",
                integrityException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al actualizar profesor {}",
                professor.getStaffNumber(),
                sqlException
            );

            throw new BusinessException(
                "Error actualizando profesor con número de personal "
                + professor.getStaffNumber(),
                sqlException
            );
        }

        return wasUpdated;
    }

    @Override
    public Optional<ProfessorDTO> findByStaffNumber(
            String staffNumber
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            staffNumber,
            "El número de personal no puede estar vacío."
        );

        Optional<ProfessorDTO> professor;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectProfessorStatement =
                    connection.prepareStatement(
                        SELECT_PROFESSOR_BY_STAFF_NUMBER_QUERY
                    )) {
            selectProfessorStatement.setString(1, staffNumber);

            try (ResultSet resultSet =
                    selectProfessorStatement.executeQuery()) {
                professor =
                    buildOptionalProfessorWithGroups(resultSet);
            }
        } catch (
                SQLTransientConnectionException connectionException) {
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
                "Error SQL al buscar profesor",
                selectException
            );

            throw new BusinessException(
                "Error buscando profesor con número de personal "
                + staffNumber,
                selectException
            );
        }

        return professor;
    }

    @Override

    public List<ProfessorDTO> findByName(String searchName) throws BusinessException {
        InputValidator.validateNotEmpty(
            searchName,
            "El nombre del profesor no puede estar vacío."
        );

        List<ProfessorDTO> professors = new ArrayList<>();
        String searchPattern = "%" + searchName.trim() + "%";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectProfessorsStatement =
                    connection.prepareStatement(
                        SELECT_PROFESSORS_BY_NAME_QUERY
                    )) {
            selectProfessorsStatement.setString(1, searchPattern);

            try (ResultSet resultSet =
                    selectProfessorsStatement.executeQuery()) {
                while (resultSet.next()) {
                    professors.add(buildProfessor(resultSet));
                }
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
                "Error SQL al buscar profesores por nombre",
                sqlException
            );

            throw new BusinessException(
                "Error buscando profesores por nombre.",
                sqlException
            );
        }

        return List.copyOf(professors);
    }

    @Override
    public List<ProfessorDTO> findAllName()
            throws BusinessException {
        List<ProfessorDTO> professors = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllProfessorsStatement =
                    connection.prepareStatement(
                        SELECT_ALL_PROFESSORS_NAME_QUERY
                    );
             ResultSet resultSet =
                    selectAllProfessorsStatement.executeQuery()) {
            while (resultSet.next()) {
                ProfessorDTO professor = new ProfessorDTO();

                professor.setName(
                    resultSet.getString("nombre")
                );

                professors.add(professor);
            }
        } catch (
                SQLTransientConnectionException connectionException
        ) {
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
                "Error SQL al obtener la lista de profesores",
                sqlException
            );

            throw new BusinessException(
                "Error obteniendo la lista de profesores.",
                sqlException
            );
        }

        return List.copyOf(professors);
    }

    @Override
    public List<ProfessorDTO> findAll()
            throws BusinessException {
        List<ProfessorDTO> professors = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllProfessorsStatement =
                    connection.prepareStatement(
                        SELECT_ALL_PROFESSORS_QUERY
                    );
             ResultSet resultSet =
                    selectAllProfessorsStatement.executeQuery()) {
            while (resultSet.next()) {
                professors.add(buildProfessor(resultSet));
            }
        } catch (
                SQLTransientConnectionException connectionException
        ) {
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
                "Error SQL al obtener la lista de profesores",
                sqlException
            );

            throw new BusinessException(
                "No se pudo obtener la lista de profesores.",
                sqlException
            );
        }

        return List.copyOf(professors);
    }

    @Override
    public Optional<ProfessorDTO> findCoordinator()
            throws BusinessException {
        Optional<ProfessorDTO> coordinator;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectCoordinatorStatement =
                    connection.prepareStatement(
                        SELECT_COORDINATOR_QUERY
                    );
             ResultSet resultSet =
                    selectCoordinatorStatement.executeQuery()) {
            coordinator =
                buildOptionalCoordinator(resultSet);
        } catch (
                SQLTransientConnectionException connectionException
        ) {
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
                "Error SQL al obtener coordinador",
                selectException
            );

            throw new BusinessException(
                "Error obteniendo coordinador.",
                selectException
            );
        }

        return coordinator;
    }

    @Override
    public int countAll() throws BusinessException {
        int totalProfessors = 0;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectCountProfessorStatement =
                    connection.prepareStatement(
                        SELECT_COUNT_PROFESSORS_QUERY
                    );
             ResultSet resultSet =
                    selectCountProfessorStatement.executeQuery()) {
            if (resultSet.next()) {
                totalProfessors =
                    resultSet.getInt("total");
            }
        } catch (
                SQLTransientConnectionException connectionException
        ) {
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
                "Error SQL al contar profesores",
                sqlException
            );

            throw new BusinessException(
                "Error al obtener el total de profesores.",
                sqlException
            );
        }

        return totalProfessors;
    }

    private void validateProfessor(ProfessorDTO professor) throws BusinessException {
        ProfessorValidator validator = new ProfessorValidator();

        validator.validateStaffNumber(professor.getStaffNumber());
    }

    private Optional<ProfessorDTO> buildOptionalProfessorWithGroups(
            ResultSet resultSet
    ) throws SQLException {
        Optional<ProfessorDTO> professor;

        if (resultSet.next()) {

            professor = Optional.of(
                buildProfessorWithGroups(resultSet)
            );
        } else {
            professor = Optional.empty();
        }

        return professor;
    }

    private Optional<ProfessorDTO> buildOptionalCoordinator(
            ResultSet resultSet
    ) throws SQLException {
        Optional<ProfessorDTO> coordinator;

        if (resultSet.next()) {
            coordinator = Optional.of(
                buildCoordinator(resultSet)
            );
        } else {
            coordinator = Optional.empty();
        }

        return coordinator;
    }

    private ProfessorDTO buildProfessor(ResultSet resultSet) throws SQLException {
        ProfessorDTO professor = new ProfessorDTO();

        professor.setStaffNumber(resultSet.getString("numero_personal"));
        professor.setId(resultSet.getInt("id"));
        professor.setInstitutionalEmail(resultSet.getString("correo_institucional"));
        professor.setIsCoordinator(resultSet.getBoolean("es_coordinador"));
        professor.setName(resultSet.getString("nombre"));
        professor.setFirstSurname(resultSet.getString("apellido_paterno"));
        professor.setSecondSurname(resultSet.getString("apellido_materno"));
        professor.setIsActive(resultSet.getBoolean("activo"));

        return professor;
    }

    private ProfessorDTO buildProfessorWithGroups(
            ResultSet resultSet
    ) throws SQLException {
        ProfessorDTO professor = buildProfessor(resultSet);
        professor.setGroups(resultSet.getInt("grupos"));

        return professor;
    }

    private ProfessorDTO buildCoordinator(
            ResultSet resultSet
    ) throws SQLException {
        ProfessorDTO professor =
            buildProfessor(resultSet);

        professor.setRole(
            UserRole.fromDatabaseValue(
                resultSet.getString("rol")
            )
        );

        return professor;
    }
}