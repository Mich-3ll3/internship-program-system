package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

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

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
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
        + "u.apellido_paterno, u.apellido_materno, u.activo, u.contrasena, "
        + "(SELECT GROUP_CONCAT("
        + "CONCAT(ea.NRC, ' - ', ea.periodo_escolar, ' - Seccion ', ea.seccion) "
        + "ORDER BY ea.periodo_escolar DESC, ea.NRC ASC SEPARATOR '\n') "
        + "FROM EXPERIENCIA_EDUCATIVA ea "
        + "WHERE ea.profesor_id = u.id AND ea.activa = TRUE) "
        + "AS experiencia_actual, "
        + "(SELECT GROUP_CONCAT("
        + "CONCAT(eh.NRC, ' - ', eh.periodo_escolar, ' - Seccion ', "
        + "eh.seccion, ' - ', IF(eh.activa, 'Activa', 'Inactiva')) "
        + "ORDER BY eh.periodo_escolar DESC, eh.NRC ASC SEPARATOR '\n') "
        + "FROM EXPERIENCIA_EDUCATIVA eh "
        + "WHERE eh.profesor_id = u.id) AS historial_experiencias "
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
        + "u.activo, u.contrasena, COUNT(e.profesor_id) AS grupos, "
        + "(SELECT GROUP_CONCAT("
        + "CONCAT(ea.NRC, ' - ', ea.periodo_escolar, ' - Seccion ', ea.seccion) "
        + "ORDER BY ea.periodo_escolar DESC, ea.NRC ASC SEPARATOR '\n') "
        + "FROM EXPERIENCIA_EDUCATIVA ea "
        + "WHERE ea.profesor_id = u.id AND ea.activa = TRUE) "
        + "AS experiencia_actual, "
        + "(SELECT GROUP_CONCAT("
        + "CONCAT(eh.NRC, ' - ', eh.periodo_escolar, ' - Seccion ', "
        + "eh.seccion, ' - ', IF(eh.activa, 'Activa', 'Inactiva')) "
        + "ORDER BY eh.periodo_escolar DESC, eh.NRC ASC SEPARATOR '\n') "
        + "FROM EXPERIENCIA_EDUCATIVA eh "
        + "WHERE eh.profesor_id = u.id) AS historial_experiencias "
        + "FROM PROFESOR p "
        + "JOIN USUARIO u ON p.usuario_id = u.id "
        + "LEFT JOIN EXPERIENCIA_EDUCATIVA e "
        + "ON p.usuario_id = e.profesor_id "
        + "WHERE p.numero_personal = ? "
        + "GROUP BY p.numero_personal, p.es_coordinador, "
        + "u.id, u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo, u.contrasena";

    private static final String SELECT_ALL_PROFESSORS_QUERY =
        "SELECT p.numero_personal, p.es_coordinador, u.id, "
        + "u.correo_institucional, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo, u.contrasena "
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
    
    private static final String SELECT_COUNT_ACTIVE_PROFESSORS_QUERY =
        "SELECT COUNT(*) AS total FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id WHERE u.activo = TRUE AND u.contrasena IS NOT NULL";

    private static final String SELECT_COUNT_INACTIVE_PROFESSORS_QUERY =
        "SELECT COUNT(*) AS total FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id WHERE u.activo = FALSE AND u.contrasena IS NOT NULL";

    private static final String SELECT_COUNT_PENDING_PROFESSORS_QUERY =
        "SELECT COUNT(*) AS total FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id WHERE u.contrasena IS NULL";
    
    private static final String EXISTS_COORDINATOR_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM PROFESOR "
        + "WHERE es_coordinador = TRUE";

    private static final String EXISTS_ACTIVE_EDUCATIONAL_EXPERIENCE_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_EDUCATIVA "
        + "WHERE profesor_id = ? "
        + "AND activa = TRUE";

    @Override
    public boolean create(ProfessorDTO professor, Connection connection) throws BusinessException, DataAccessException {
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
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            professor,
            "ProfessorDTO no puede ser nulo."
        );

        validateProfessor(professor);

        boolean wasUpdated;

        try (Connection connection = DatabaseManager.getConnection();
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
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotEmpty(
            staffNumber,
            "El número de personal no puede estar vacío."
        );

        Optional<ProfessorDTO> professor;

        try (Connection connection = DatabaseManager.getConnection();
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

    public List<ProfessorDTO> findByName(String searchName) throws BusinessException, DataAccessException {
        InputValidator.validateNotEmpty(
            searchName,
            "El nombre del profesor no puede estar vacío."
        );

        List<ProfessorDTO> professors = new ArrayList<>();
        String searchPattern = "%" + searchName.trim() + "%";

        try (Connection connection = DatabaseManager.getConnection();
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
            throws BusinessException, DataAccessException {
        List<ProfessorDTO> professors = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
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
            throws BusinessException, DataAccessException {
        List<ProfessorDTO> professors = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
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
            throws BusinessException, DataAccessException {
        Optional<ProfessorDTO> coordinator;

        try (Connection connection = DatabaseManager.getConnection();
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
    public int countAll() throws BusinessException, DataAccessException {
        int totalProfessors = 0;

        try (Connection connection = DatabaseManager.getConnection();
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

    @Override
    public int countActive() throws BusinessException, DataAccessException {
        int totalActive = 0;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COUNT_ACTIVE_PROFESSORS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                totalActive = resultSet.getInt("total");
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new DataAccessException("El servicio de almacenamiento no se encuentra disponible de momento.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al contar profesores activos", sqlException);
            throw new DataAccessException("Error al obtener el total de profesores activos.", sqlException);
        }

        return totalActive;
    }

    @Override
    public int countInactive() throws BusinessException, DataAccessException {
        int totalInactive = 0;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COUNT_INACTIVE_PROFESSORS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                totalInactive = resultSet.getInt("total");
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new DataAccessException("El servicio de almacenamiento no se encuentra disponible de momento.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al contar profesores inactivos", sqlException);
            throw new DataAccessException("Error al obtener el total de profesores inactivos.", sqlException);
        }

        return totalInactive;
    }

    @Override
    public int countPending() throws BusinessException, DataAccessException {
        int totalPending = 0;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COUNT_PENDING_PROFESSORS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                totalPending = resultSet.getInt("total");
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new DataAccessException("El servicio de almacenamiento no se encuentra disponible de momento.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al contar profesores pendientes", sqlException);
            throw new DataAccessException("Error al obtener el total de profesores pendientes.", sqlException);
        }

        return totalPending;
    }
    
    @Override
    public boolean existsCoordinator() throws BusinessException, DataAccessException {
        boolean coordinatorExists = false;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(EXISTS_COORDINATOR_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                coordinatorExists = resultSet.getInt("total") > 0;
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
                "Error SQL al verificar existencia de coordinador",
                sqlException
            );

            throw new BusinessException(
                "Error al verificar si ya existe coordinador.",
                sqlException
            );
        }

        return coordinatorExists;
    }

    @Override
    public boolean hasActiveEducationalExperience(
            int professorId
    ) throws BusinessException, DataAccessException {
        InputValidator.validatePositive(
            professorId,
            "El identificador del profesor debe ser positivo."
        );

        boolean hasActiveEducationalExperience;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        EXISTS_ACTIVE_EDUCATIONAL_EXPERIENCE_QUERY
                    )) {
            statement.setInt(
                1,
                professorId
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                hasActiveEducationalExperience =
                    resultSet.next()
                    && resultSet.getInt("total") > 0;
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
                "Error SQL al verificar experiencia educativa activa.",
                sqlException
            );

            throw new BusinessException(
                "Error al verificar si el profesor tiene una experiencia activa.",
                sqlException
            );
        }

        return hasActiveEducationalExperience;
    }

    private void validateProfessor(ProfessorDTO professor) throws BusinessException, DataAccessException {
        ProfessorValidator validator = new ProfessorValidator();

        validator.validateStaffNumber(professor.getStaffNumber());
        validator.validateProfessorForUpdate(professor);
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
        try {
            professor.setPassword(resultSet.getString("contrasena"));
        } catch (SQLException sqlException) {
            LOGGER.debug(
                "La consulta de profesor no incluye contrasena.",
                sqlException
            );
        }
        setProfessorEducationalExperienceData(
            professor,
            resultSet
        );

        return professor;
    }

    private void setProfessorEducationalExperienceData(
            ProfessorDTO professor,
            ResultSet resultSet
    ) throws SQLException {
        try {
            professor.setCurrentEducationalExperience(
                resultSet.getString("experiencia_actual")
            );
            professor.setEducationalExperienceHistory(
                resultSet.getString("historial_experiencias")
            );
        } catch (SQLException sqlException) {
            LOGGER.debug(
                "La consulta de profesor no incluye historial de NRC.",
                sqlException
            );
        }
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