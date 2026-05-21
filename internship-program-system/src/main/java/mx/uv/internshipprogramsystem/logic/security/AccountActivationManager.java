package mx.uv.internshipprogramsystem.logic.security;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dao.ActivationTokenDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.PasswordValidator;

public class AccountActivationManager {
    private final ActivationTokenDAO activationTokenDAO;
    private final UserDAO userDAO;
    private final SecurityManager securityManager;
    private final PasswordValidator passwordValidator;

    public AccountActivationManager() {
        activationTokenDAO = new ActivationTokenDAO();
        userDAO = new UserDAO();
        securityManager = new SecurityManager();
        passwordValidator = new PasswordValidator();
    }

    public void activateAccount(
            String token,
            String password,
            String confirmPassword
    ) throws BusinessException {
        InputValidator.validateNotEmpty( token,"El token de activación no puede estar vacío.");

        passwordValidator.validatePassword(password);
        validatePasswordConfirmation(password, confirmPassword);

        String tokenHash = securityManager.hashToken(token);

        Optional<ActivationTokenDTO> optionalActivationToken =
            activationTokenDAO.findByTokenHash(tokenHash);

        ActivationTokenDTO activationToken =
            getValidActivationToken(optionalActivationToken);

        String passwordHash = securityManager.hashPassword(password);

        activateUserAccount(activationToken.getUserId(),passwordHash);

        activationTokenDAO.markAsUsed(activationToken.getId());
    }

    private ActivationTokenDTO getValidActivationToken(
            Optional<ActivationTokenDTO> optionalActivationToken
    ) throws BusinessException {
        ActivationTokenDTO activationToken;

        if (optionalActivationToken.isEmpty()) {
            throw new BusinessException("El token de activación es inválido.");
        }

        activationToken = optionalActivationToken.get();

        validateTokenIsNotUsed(activationToken);
        validateTokenExpiration(activationToken);

        return activationToken;
    }

    private void validatePasswordConfirmation(
            String password,
            String confirmPassword
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            confirmPassword,
            "La confirmación de contraseña no puede estar vacía."
        );

        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Las contraseñas no coinciden.");
        }
    }

    private void validateTokenIsNotUsed(
            ActivationTokenDTO activationToken
    ) throws BusinessException {
        if (activationToken.isUsed()) {
            throw new BusinessException("El token de activación ya fue utilizado.");
        }
    }

    private void validateTokenExpiration(
            ActivationTokenDTO activationToken
    ) throws BusinessException {
        Timestamp currentTimestamp = Timestamp.from(Instant.now());

        if (activationToken.getExpirationDate().before(currentTimestamp)) {
            throw new BusinessException("El token de activación ha expirado.");
        }
    }

    private void activateUserAccount(int userId, String passwordHash) throws BusinessException {
        boolean wasActivated = userDAO.activateAccount(userId, passwordHash);

        if (!wasActivated) {
            throw new BusinessException("No se pudo activar la cuenta del usuario.");
        }
    }
}