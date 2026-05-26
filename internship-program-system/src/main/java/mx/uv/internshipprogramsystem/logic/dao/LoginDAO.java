package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.ILoginDAO;
import mx.uv.internshipprogramsystem.logic.security.LoginAttemptManager;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class LoginDAO implements ILoginDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LoginDAO.class);

    private static final String SELECT_USER_BY_EMAIL_FOR_LOGIN_QUERY =
        "SELECT u.id, u.correo_institucional, u.contrasena, u.nombre, "
        + "u.apellido_paterno, u.apellido_materno, u.activo, u.rol, "
        + "u.intentos_fallidos_login, u.fecha_bloqueo_login, "
        + "e.matricula, p.numero_personal, p.es_coordinador "
        + "FROM USUARIO u "
        + "LEFT JOIN ESTUDIANTE e ON u.id = e.usuario_id "
        + "LEFT JOIN PROFESOR p ON u.id = p.usuario_id "
        + "WHERE u.correo_institucional = ?";

    private static final String INCREMENT_FAILED_ATTEMPTS_QUERY =
        "UPDATE USUARIO "
        + "SET intentos_fallidos_login = intentos_fallidos_login + 1 "
        + "WHERE id = ?";

    private static final String LOCK_USER_LOGIN_QUERY =
        "UPDATE USUARIO "
        + "SET fecha_bloqueo_login = NOW() "
        + "WHERE id = ?";

    private static final String RESET_FAILED_ATTEMPTS_QUERY =
        "UPDATE USUARIO "
        + "SET intentos_fallidos_login = 0, fecha_bloqueo_login = NULL "
        + "WHERE id = ?";

    public UserDTO login(
            String email,
            String plainPassword
    ) throws BusinessException {
        validateLoginData(email, plainPassword);

        UserDTO loggedUser;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement loginUserStatement =
                 connection.prepareStatement(
                     SELECT_USER_BY_EMAIL_FOR_LOGIN_QUERY
                 )) {
            loginUserStatement.setString(1, email);

            try (ResultSet resultSet =
                    loginUserStatement.executeQuery()) {
                loggedUser = buildLoggedUser(
                    resultSet,
                    plainPassword
                );
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
                "Error SQL al verificar inicio de sesión para usuario {}",
                maskEmailForLog(email),
                sqlException
            );

            throw new BusinessException(
                "Error al verificar el inicio de sesión.",
                sqlException
            );
        }

        return loggedUser;
    }

    public boolean incrementFailedLoginAttempts(
            int userId
    ) throws BusinessException {
        boolean wasIncremented;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement incrementAttemptsStatement =
                 connection.prepareStatement(
                     INCREMENT_FAILED_ATTEMPTS_QUERY
                 )) {
            incrementAttemptsStatement.setInt(1, userId);

            wasIncremented =
                incrementAttemptsStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al registrar intento fallido",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al registrar intento fallido",
                sqlException
            );

            throw new BusinessException(
                "Error al registrar intento fallido.",
                sqlException
            );
        }

        return wasIncremented;
    }

    public boolean resetFailedLoginAttempts(
            int userId
    ) throws BusinessException {
        boolean wasReset;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement resetAttemptsStatement =
                 connection.prepareStatement(
                     RESET_FAILED_ATTEMPTS_QUERY
                 )) {
            resetAttemptsStatement.setInt(1, userId);

            wasReset = resetAttemptsStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al reiniciar intentos fallidos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al reiniciar intentos fallidos",
                sqlException
            );

            throw new BusinessException(
                "Error al reiniciar intentos fallidos.",
                sqlException
            );
        }

        return wasReset;
    }

    public boolean lockUserLogin(
            int userId
    ) throws BusinessException {
        boolean wasLocked;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement lockUserStatement =
                 connection.prepareStatement(
                     LOCK_USER_LOGIN_QUERY
                 )) {
            lockUserStatement.setInt(1, userId);

            wasLocked = lockUserStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al bloquear cuenta",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al bloquear cuenta temporalmente",
                sqlException
            );

            throw new BusinessException(
                "Error al bloquear temporalmente la cuenta.",
                sqlException
            );
        }

        return wasLocked;
    }

    private void validateLoginData(
            String email,
            String plainPassword
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            email,
            "El correo institucional es obligatorio."
        );

        InputValidator.validateNotEmpty(
            plainPassword,
            "La contraseña es obligatoria."
        );

        UserValidator userValidator = new UserValidator();
        userValidator.validateEmailFormat(email);
    }

    private UserDTO buildLoggedUser(
            ResultSet resultSet,
            String plainPassword
    ) throws SQLException, BusinessException {
        UserDTO loggedUser;

        if (!resultSet.next()) {
            throw new BusinessException("Credenciales inválidas.");
        }

        validateLoginLock(resultSet);
        validateUserIsActive(resultSet);
        validateStoredPassword(resultSet, plainPassword);

        resetFailedLoginAttempts(resultSet.getInt("id"));

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
            registerFailedLoginAttempt(resultSet);

            throw new BusinessException("Credenciales inválidas.");
        }
    }

    private void validateLoginLock(
            ResultSet resultSet
    ) throws SQLException, BusinessException {
        LoginAttemptManager loginAttemptManager =
            new LoginAttemptManager();

        loginAttemptManager.validateAccountIsNotLocked(
            resultSet.getInt("intentos_fallidos_login"),
            resultSet.getTimestamp("fecha_bloqueo_login")
        );
    }

    private void validateUserIsActive(
            ResultSet resultSet
    ) throws SQLException, BusinessException {
        if (!resultSet.getBoolean("activo")) {
            throw new BusinessException("La cuenta está desactivada.");
        }
    }

    private void registerFailedLoginAttempt(
            ResultSet resultSet
    ) throws SQLException, BusinessException {
        int userId = resultSet.getInt("id");
        int failedAttempts =
            resultSet.getInt("intentos_fallidos_login");

        LoginAttemptManager loginAttemptManager =
            new LoginAttemptManager();

        incrementFailedLoginAttempts(userId);

        if (loginAttemptManager.shouldLockAccount(failedAttempts)) {
            lockUserLogin(userId);
        }
    }

    private UserDTO buildUserByRole(
            ResultSet resultSet
    ) throws SQLException, BusinessException {
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
        professor.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );
        professor.setName(resultSet.getString("nombre"));
        professor.setFirstSurname(
            resultSet.getString("apellido_paterno")
        );
        professor.setSecondSurname(
            resultSet.getString("apellido_materno")
        );
        professor.setIsActive(resultSet.getBoolean("activo"));
        professor.setStaffNumber(
            resultSet.getString("numero_personal")
        );
        professor.setIsCoordinator(
            resultSet.getBoolean("es_coordinador")
        );
        professor.setRole(UserRole.PROFESSOR);

        return professor;
    }

    private InternDTO buildIntern(
            ResultSet resultSet
    ) throws SQLException {
        InternDTO intern = new InternDTO();

        intern.setId(resultSet.getInt("id"));
        intern.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );
        intern.setName(resultSet.getString("nombre"));
        intern.setFirstSurname(
            resultSet.getString("apellido_paterno")
        );
        intern.setSecondSurname(
            resultSet.getString("apellido_materno")
        );
        intern.setIsActive(resultSet.getBoolean("activo"));
        intern.setEnrollmentNumber(resultSet.getString("matricula"));
        intern.setRole(UserRole.STUDENT);

        return intern;
    }

    private UserDTO buildAdmin(
            ResultSet resultSet
    ) throws SQLException {
        UserDTO admin = new UserDTO();

        admin.setId(resultSet.getInt("id"));
        admin.setInstitutionalEmail(
            resultSet.getString("correo_institucional")
        );
        admin.setName(resultSet.getString("nombre"));
        admin.setFirstSurname(
            resultSet.getString("apellido_paterno")
        );
        admin.setSecondSurname(
            resultSet.getString("apellido_materno")
        );
        admin.setIsActive(resultSet.getBoolean("activo"));
        admin.setRole(UserRole.ADMINISTRATOR);

        return admin;
    }

    private String maskEmailForLog(String email) {
        String maskedEmail = "correo-no-disponible";

        if (email != null && email.contains("@")) {
            int atIndex = email.indexOf("@");
            String domain = email.substring(atIndex);

            maskedEmail = "***" + domain;
        }

        return maskedEmail;
    }
}