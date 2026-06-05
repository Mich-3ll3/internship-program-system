package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IUserDAO;
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

    private static final String SELECT_USER_BY_EMAIL_QUERY =
        "SELECT id, correo_institucional, nombre, "
        + "apellido_paterno, apellido_materno, "
        + "activo, rol "
        + "FROM USUARIO "
        + "WHERE correo_institucional = ?";
    
    private static final String UPDATE_PASSWORD_QUERY =
        "UPDATE USUARIO "
        + "SET contrasena = ? "
        + "WHERE id = ?";

    private static final String CHANGE_USER_STATUS_QUERY =
        "UPDATE USUARIO SET activo = ? WHERE correo_institucional = ?";

    private static final String SELECT_COUNT_ACTIVE_USERS_QUERY =
        "SELECT COUNT(*) AS totalActivos FROM USUARIO WHERE activo = true";

    private static final String ACTIVATE_USER_ACCOUNT_QUERY =
        "UPDATE USUARIO SET contrasena = ?, activo = true WHERE id = ?";
    
    private static final String CHANGE_USER_STATUS_BY_ID_QUERY =
        "UPDATE USUARIO SET activo = ? WHERE id = ?";

    @Override
    public int create(UserDTO user, Connection connection) throws BusinessException {
        InputValidator.validateNotNull(connection, "La conexión no puede ser nula.");
        validateUserForCreation(user);

        int generatedUserId;

        try (PreparedStatement insertUserStatement =
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
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
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

    public int create(UserDTO user) throws BusinessException {
        int generatedUserId;

        try (Connection connection = DataBaseManager.getConnection()) {
            generatedUserId = create(user, connection);
        } catch (SQLException exception) {
            throw new BusinessException(
                "Error al crear el usuario.",
                exception
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

    public Optional<UserDTO> findByInstitutionalEmail(
        String institutionalEmail
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            institutionalEmail,
            "El correo institucional no puede estar vacío."
        );

        Optional<UserDTO> optionalUser;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectUserStatement =
                 connection.prepareStatement(SELECT_USER_BY_EMAIL_QUERY)) {

            selectUserStatement.setString(1, institutionalEmail);

            try (ResultSet resultSet = selectUserStatement.executeQuery()) {
                optionalUser = buildOptionalUser(resultSet);
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
                "Error SQL al buscar usuario por correo {}",
                institutionalEmail,
                sqlException
            );

            throw new BusinessException(
                "Error al buscar el usuario.",
                sqlException
            );
        }

        return optionalUser;
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
    
    public boolean updatePassword(
        int userId,
        String passwordHash
    ) throws BusinessException {

        InputValidator.validatePositive(
            userId,
            "El identificador del usuario no es válido."
        );

        InputValidator.validateNotEmpty(
            passwordHash,
            "El hash de la contraseña no puede estar vacío."
        );

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updatePasswordStatement =
                 connection.prepareStatement(
                     UPDATE_PASSWORD_QUERY
                 )) {

            updatePasswordStatement.setString(
                1,
                passwordHash
            );

            updatePasswordStatement.setInt(
                2,
                userId
            );

            wasUpdated =
                updatePasswordStatement.executeUpdate() > 0;

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
                "Error SQL al actualizar contraseña",
                sqlException
            );

            throw new BusinessException(
                "Error al actualizar la contraseña.",
                sqlException
            );
        }

        return wasUpdated;
    }
    
    private Optional<UserDTO> buildOptionalUser(
        ResultSet resultSet
    ) throws SQLException, BusinessException {
        Optional<UserDTO> optionalUser;

        if (resultSet.next()) {
            optionalUser = Optional.of(buildUser(resultSet));
        } else {
            optionalUser = Optional.empty();
        }

        return optionalUser;
    }

    private UserDTO buildUser(
            ResultSet resultSet
    ) throws SQLException, BusinessException {
        UserDTO user = new UserDTO();

        user.setId(resultSet.getInt("id"));
        user.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );
        user.setName(resultSet.getString("nombre"));
        user.setFirstSurname(resultSet.getString("apellido_paterno"));
        user.setSecondSurname(resultSet.getString("apellido_materno"));
        user.setIsActive(resultSet.getBoolean("activo"));
        user.setRole(
            UserRole.fromDatabaseValue(resultSet.getString("rol"))
        );

        return user;
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
}