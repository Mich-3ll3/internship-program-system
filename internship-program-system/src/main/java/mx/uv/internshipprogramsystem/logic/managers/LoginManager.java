package mx.uv.internshipprogramsystem.logic.managers;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import mx.uv.internshipprogramsystem.logic.dao.LoginDAO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.InvalidCredentialsException;
import mx.uv.internshipprogramsystem.logic.security.LoginAttemptManager;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class LoginManager {
    private final LoginDAO loginDAO;
    private final LoginAttemptManager loginAttemptManager;
    private final SecurityManager securityManager;

    public LoginManager() {
        this.loginDAO = new LoginDAO();
        this.loginAttemptManager = new LoginAttemptManager();
        this.securityManager = new SecurityManager();
    }

    public LoginManager(LoginDAO loginDAO, LoginAttemptManager loginAttemptManager, SecurityManager securityManager) {
        this.loginDAO = loginDAO;
        this.loginAttemptManager = loginAttemptManager;
        this.securityManager = securityManager;
    }

    public UserDTO login(String email, String plainPassword) throws BusinessException, DataAccessException {
        validateLoginData(email, plainPassword);

        UserDTO user = loginDAO.findUserByEmail(email);
        if (user == null) {
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }

        loginAttemptManager.validateAccountIsNotLocked(
            user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0,
            user.getLoginLockDate()
        );

        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new BusinessException("La cuenta está desactivada.");
        }

        String passwordHash = user.getPassword();
        if (passwordHash == null) {
            throw new BusinessException("La cuenta aún no ha sido activada.");
        }

        if (!securityManager.verifyPassword(plainPassword, passwordHash)) {
            registerFailedLoginAttempt(user);
            throw new InvalidCredentialsException("Correo o contraseña incorrectos.");
        }

        loginDAO.resetFailedLoginAttempts(user.getId());

        return user;
    }

    private void validateLoginData(String email, String plainPassword) throws BusinessException, DataAccessException {
        InputValidator.validateNotEmpty(email, "El correo institucional es obligatorio.");
        InputValidator.validateNotEmpty(plainPassword, "La contraseña es obligatoria.");

        UserValidator userValidator = new UserValidator();
        userValidator.validateEmailFormat(email);
    }

    private void registerFailedLoginAttempt(UserDTO user) throws BusinessException, DataAccessException {
        int userId = user.getId();
        int failedAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;

        loginDAO.incrementFailedLoginAttempts(userId);

        if (loginAttemptManager.shouldLockAccount(failedAttempts)) {
            loginDAO.lockUserLogin(userId);
        }
    }
}