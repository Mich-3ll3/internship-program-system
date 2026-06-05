package mx.uv.internshipprogramsystem.logic.managers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dao.PasswordRecoveryTokenDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.PasswordValidator;

public class PasswordRecoveryManager {

    private final PasswordRecoveryTokenDAO passwordRecoveryTokenDAO;
    private final UserDAO userDAO;
    private final SecurityManager securityManager;
    private final PasswordValidator passwordValidator;

    public PasswordRecoveryManager() {
        passwordRecoveryTokenDAO = new PasswordRecoveryTokenDAO();
        userDAO = new UserDAO();
        securityManager = new SecurityManager();
        passwordValidator = new PasswordValidator();
    }

    public void resetPassword(
            String recoveryToken,
            String newPassword,
            String confirmPassword
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            recoveryToken,
            "El token de recuperación no puede estar vacío."
        );

        passwordValidator.validatePassword(newPassword);
        validatePasswordConfirmation(newPassword, confirmPassword);

        String tokenHash = securityManager.hashToken(recoveryToken);

        Optional<PasswordRecoveryTokenDTO> optionalRecoveryToken =
            passwordRecoveryTokenDAO.findByTokenHash(tokenHash);

        PasswordRecoveryTokenDTO passwordRecoveryToken =
            getValidRecoveryToken(optionalRecoveryToken);

        String passwordHash = securityManager.hashPassword(newPassword);

        updateUserPassword(
            passwordRecoveryToken.getUserId(),
            passwordHash
        );

        markTokenAsUsed(passwordRecoveryToken.getId());
    }

    private PasswordRecoveryTokenDTO getValidRecoveryToken(
            Optional<PasswordRecoveryTokenDTO> optionalRecoveryToken
    ) throws BusinessException {
        PasswordRecoveryTokenDTO passwordRecoveryToken;

        if (optionalRecoveryToken.isEmpty()) {
            throw new BusinessException(
                "El token de recuperación es inválido."
            );
        }

        passwordRecoveryToken = optionalRecoveryToken.get();

        validateTokenIsNotUsed(passwordRecoveryToken);
        validateTokenExpiration(passwordRecoveryToken);

        return passwordRecoveryToken;
    }

    private void validatePasswordConfirmation(
            String newPassword,
            String confirmPassword
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            confirmPassword,
            "La confirmación de contraseña no puede estar vacía."
        );

        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(
                "Las contraseñas no coinciden."
            );
        }
    }

    private void validateTokenIsNotUsed(
            PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException {
        if (passwordRecoveryToken.isUsed()) {
            throw new BusinessException(
                "El token de recuperación ya fue utilizado."
            );
        }
    }

    private void validateTokenExpiration(
            PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException {
        Timestamp currentTimestamp = Timestamp.from(Instant.now());

        if (passwordRecoveryToken.getExpirationDate().before(currentTimestamp)) {
            throw new BusinessException(
                "El token de recuperación ha expirado."
            );
        }
    }

    private void updateUserPassword(
            int userId,
            String passwordHash
    ) throws BusinessException {
        boolean wasUpdated = userDAO.updatePassword(
            userId,
            passwordHash
        );

        if (!wasUpdated) {
            throw new BusinessException(
                "No se pudo actualizar la contraseña."
            );
        }
    }

    private void markTokenAsUsed(
            int tokenId
    ) throws BusinessException {
        boolean wasMarkedAsUsed =
            passwordRecoveryTokenDAO.markAsUsed(tokenId);

        if (!wasMarkedAsUsed) {
            throw new BusinessException(
                "No se pudo marcar el token como utilizado."
            );
        }
    }
}