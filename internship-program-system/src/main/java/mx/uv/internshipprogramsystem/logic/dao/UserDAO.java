package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IUserDAO;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UserDAO implements IUserDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(UserDAO.class);

    private static final String INSERT_USER_QUERY =
        "INSERT INTO USUARIO (correo_institucional, nombre, apellido_paterno, "
        + "apellido_materno, activo, rol) "
        + "VALUES (?, ?, ?, ?, false, ?)";

    private static final String UPDATE_USER_QUERY =
        "UPDATE USUARIO SET nombre = ?, apellido_paterno = ?, "
        + "apellido_materno = ?, activo = ?, rol = ? "
        + "WHERE correo_institucional = ?";

    private static final String CHANGE_USER_STATUS_QUERY =
        "UPDATE USUARIO SET activo = ? WHERE correo_institucional = ?";

    private static final String SELECT_USER_BY_EMAIL_FOR_LOGIN_QUERY =
        "SELECT u.id, u.correo_institucional, u.contrasena, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo, u.rol, "
        + "e.matricula, p.numero_personal, p.es_coordinador "
        + "FROM USUARIO u "
        + "LEFT JOIN ESTUDIANTE e ON u.id = e.usuario_id "
        + "LEFT JOIN PROFESOR p ON u.id = p.usuario_id "
        + "WHERE u.correo_institucional = ?";

    private static final String SELECT_COUNT_ACTIVE_USERS_QUERY =
        "SELECT COUNT(*) AS totalActivos FROM USUARIO WHERE activo = true";

    private static final String ACTIVATE_USER_ACCOUNT_QUERY =
        "UPDATE USUARIO SET contrasena = ?, activo = true WHERE id = ?";
    
    private static final String CHANGE_USER_STATUS_BY_ID_QUERY =
        "UPDATE USUARIO SET activo = ? WHERE id = ?";

    @Override
    public int create(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        validateUserForCreation(user);

        int generatedUserId;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement insertUserStatement =
            connection.prepareStatement(
            INSERT_USER_QUERY,
            Statement.RETURN_GENERATED_KEYS
        )) {
            setUserData(insertUserStatement, user);
            int affectedRows = insertUserStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new BusinessException("No se pudo crear el usuario "
                    + user.getInstitutionalEmail()
                );
            }

            generatedUserId = getGeneratedUserId(insertUserStatement);
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad al crear usuario", integrityException);
            throw new BusinessException(
                "El correo institucional ya existe.", integrityException);
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error( "Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException(
                "No se pudo establecer conexión con la base de datos.", connectionException);
        } catch (SQLException insertException) {
            LOGGER.error(
                "Error SQL al crear usuario {}",
                user.getInstitutionalEmail(),
                insertException
            );
            throw new BusinessException(
                "Error creando el usuario "
                + user.getInstitutionalEmail(),
                insertException
            );
        }

        return generatedUserId;
    }

    @Override
    public boolean update(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        validateUserForUpdate(user);

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement updateUserStatement =
            connection.prepareStatement(UPDATE_USER_QUERY)) {
            updateUserStatement.setString(1, user.getName());
            updateUserStatement.setString(2, user.getFirstSurname());
            updateUserStatement.setString(3, user.getSecondSurname());
            updateUserStatement.setBoolean(4, user.getIsActive());
            updateUserStatement.setString(5,user.getRole().getDatabaseValue());
            updateUserStatement.setString(6,user.getInstitutionalEmail());

            wasUpdated = updateUserStatement.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error(
                "Violación de integridad al actualizar usuario",
                integrityException
            );
            throw new BusinessException(
                "Datos inválidos al actualizar usuario.",
                integrityException
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
        } catch (SQLException updateException) {
            LOGGER.error(
                "Error SQL al actualizar usuario {}",
                user.getInstitutionalEmail(),
                updateException
            );
            throw new BusinessException(
                "Error actualizando usuario "
                    + user.getInstitutionalEmail(),
                updateException
            );
        }

        return wasUpdated;
    }
    
    @Override
    public boolean changeStatus(int userId, boolean isActive) throws BusinessException {
        boolean wasChanged;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement changeUserStatusStatement =
                 connection.prepareStatement(CHANGE_USER_STATUS_BY_ID_QUERY)) {
            changeUserStatusStatement.setBoolean(1, isActive);
            changeUserStatusStatement.setInt(2, userId);

            wasChanged = changeUserStatusStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al cambiar estado del usuario {}",
                userId,
                sqlException
            );
            throw new BusinessException(
                "Error cambiando estado del usuario.",
                sqlException
            );
        }

        return wasChanged;
    }

    @Override
    public UserDTO login(String email, String plainPassword)
            throws BusinessException {
        InputValidator.validateNotEmpty(email, "El correo institucional es obligatorio.");
        InputValidator.validateNotEmpty(plainPassword,"La contraseña es obligatoria.");

        UserValidator validator = new UserValidator();
        validator.validateEmailFormat(email);

        UserDTO loggedUser;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement loginUserStatement =
            connection.prepareStatement(
            SELECT_USER_BY_EMAIL_FOR_LOGIN_QUERY
            )) {
            loginUserStatement.setString(1, email);

            try (ResultSet resultSet = loginUserStatement.executeQuery()) {
                loggedUser = buildLoggedUser(resultSet, plainPassword);
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al verificar login para {}", email, sqlException);
            throw new BusinessException("Error verificando login para " + email, sqlException);
        }

        return loggedUser;
    }

    @Override
    public int countActiveUsers() throws BusinessException {
        int totalActiveUsers = 0;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement selectCountActiveUsersStatement =
            connection.prepareStatement(
            SELECT_COUNT_ACTIVE_USERS_QUERY
        );
            ResultSet resultSet = selectCountActiveUsersStatement.executeQuery()) {
            if (resultSet.next()) {
                totalActiveUsers = resultSet.getInt("totalActivos");
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
                "Error SQL al contar usuarios activos",
                sqlException
            );
            throw new BusinessException(
                "Error al obtener el total de usuarios activos.",
                sqlException
            );
        }

        return totalActiveUsers;
    }

    @Override
    public boolean activateAccount(int userId, String passwordHash) throws BusinessException {
        boolean wasActivated;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement activateUserAccountStatement =
            connection.prepareStatement(
            ACTIVATE_USER_ACCOUNT_QUERY
        )) {
            activateUserAccountStatement.setString(1, passwordHash);
            activateUserAccountStatement.setInt(2, userId);

            wasActivated = activateUserAccountStatement.executeUpdate() > 0;
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
                "Error SQL al activar cuenta de usuario",
                sqlException
            );
            throw new BusinessException(
                "Error al activar la cuenta del usuario.",
                sqlException
            );
        }

        return wasActivated;
    }

    private void validateUserForCreation(UserDTO user) throws BusinessException {
        UserValidator validator = new UserValidator();
        validator.validateUserForCreation(user);
    }

    private void validateUserForUpdate(UserDTO user) throws BusinessException {
        UserValidator validator = new UserValidator();
        validator.validateUserForUpdate(user);
    }

    private void setUserData(PreparedStatement statement, UserDTO user) throws SQLException {
        statement.setString(1, user.getInstitutionalEmail());
        statement.setString(2, user.getName());
        statement.setString(3, user.getFirstSurname());
        statement.setString(4, user.getSecondSurname());
        statement.setString(5, user.getRole().getDatabaseValue());
    }

    private int getGeneratedUserId(PreparedStatement insertUserStatement)
            throws SQLException, BusinessException {
        int generatedUserId;

        try (ResultSet generatedKeys = insertUserStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                generatedUserId = generatedKeys.getInt(1);
            } else {
                throw new BusinessException(
                    "No se pudo obtener el ID del usuario creado."
                );
            }
        }

        return generatedUserId;
    }

    private UserDTO buildLoggedUser(ResultSet resultSet, String plainPassword)
        throws SQLException, BusinessException {
        UserDTO loggedUser;

        if (!resultSet.next()) {
            throw new BusinessException("Credenciales inválidas.");
        }

        validateUserIsActive(resultSet);
        validateStoredPassword(resultSet, plainPassword);

        loggedUser = buildUserByRole(resultSet);

        return loggedUser;
    }

    private void validateStoredPassword(
            ResultSet resultSet,
            String plainPassword
    ) throws SQLException, BusinessException {
        String passwordHash = resultSet.getString("contrasena");

        if (passwordHash == null) {
            throw new BusinessException(
                "La cuenta aún no ha sido activada."
            );
        }

        SecurityManager securityManager = new SecurityManager();

        if (!securityManager.verifyPassword(plainPassword, passwordHash)) {
            throw new BusinessException("Credenciales inválidas.");
        }
    }

    private void validateUserIsActive(ResultSet resultSet) throws SQLException, BusinessException {
        if (!resultSet.getBoolean("activo")) {
            throw new BusinessException("La cuenta está desactivada.");
        }
    }

    private UserDTO buildUserByRole(ResultSet resultSet) throws SQLException, BusinessException {
        UserDTO user;
        String role = resultSet.getString("rol");

        switch (UserRole.fromDatabaseValue(role)) {
            case PROFESSOR:
                user = buildProfessor(resultSet);
                break;
            case STUDENT:
                user = buildIntern(resultSet);
                break;
            case ADMINISTRATOR:
                user = buildAdmin(resultSet);
                break;
            default:
                throw new BusinessException("Rol de usuario inválido.");
        }

        return user;
    }

    private ProfessorDTO buildProfessor(ResultSet resultSet) throws SQLException {
        ProfessorDTO professor = new ProfessorDTO();

        professor.setId(resultSet.getInt("id"));
        professor.setInstitutionalEmail(resultSet.getString("correo_institucional"));
        professor.setName(resultSet.getString("nombre"));
        professor.setFirstSurname(resultSet.getString("apellido_paterno"));
        professor.setSecondSurname(resultSet.getString("apellido_materno"));
        professor.setIsActive(resultSet.getBoolean("activo"));
        professor.setStaffNumber(resultSet.getString("numero_personal"));
        professor.setIsCoordinator(resultSet.getBoolean("es_coordinador"));
        professor.setRole(UserRole.PROFESSOR);

        return professor;
    }

    private InternDTO buildIntern(ResultSet resultSet) throws SQLException {
        InternDTO intern = new InternDTO();

        intern.setId(resultSet.getInt("id"));
        intern.setInstitutionalEmail(resultSet.getString("correo_institucional"));
        intern.setName(resultSet.getString("nombre"));
        intern.setFirstSurname(resultSet.getString("apellido_paterno"));
        intern.setSecondSurname(resultSet.getString("apellido_materno"));
        intern.setIsActive(resultSet.getBoolean("activo"));
        intern.setEnrollmentNumber(resultSet.getString("matricula"));
        intern.setRole(UserRole.STUDENT);

        return intern;
    }

    private UserDTO buildAdmin(ResultSet resultSet) throws SQLException {
        UserDTO admin = new UserDTO();

        admin.setId(resultSet.getInt("id"));
        admin.setInstitutionalEmail(resultSet.getString("correo_institucional"));
        admin.setName(resultSet.getString("nombre"));
        admin.setFirstSurname(resultSet.getString("apellido_paterno"));
        admin.setSecondSurname(resultSet.getString("apellido_materno"));
        admin.setIsActive(resultSet.getBoolean("activo"));
        admin.setRole(UserRole.ADMINISTRATOR);

        return admin;
    }
}