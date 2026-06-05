package mx.uv.internshipprogramsystem.logic.managers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dao.PasswordRecoveryTokenDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class PasswordRecoveryRequestManager {

    private static final int RECOVERY_TOKEN_EXPIRATION_HOURS = 1;

    private final UserDAO userDAO;
    private final PasswordRecoveryTokenDAO passwordRecoveryTokenDAO;
    private final SecurityManager securityManager;
    private final RecoveryEmailManager recoveryEmailManager;

    public PasswordRecoveryRequestManager() {
        userDAO = new UserDAO();
        passwordRecoveryTokenDAO = new PasswordRecoveryTokenDAO();
        securityManager = new SecurityManager();
        recoveryEmailManager = new RecoveryEmailManager();
    }

    public void sendRecoveryToken(
            String institutionalEmail
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            institutionalEmail,
            "El correo institucional no puede estar vacío."
        );

        Optional<UserDTO> optionalUser =
            userDAO.findByInstitutionalEmail(institutionalEmail);

        UserDTO user = getValidUser(optionalUser);

        invalidatePreviousTokens(user.getId());

        String recoveryToken = securityManager.generateActivationToken();
        String tokenHash = securityManager.hashToken(recoveryToken);

        PasswordRecoveryTokenDTO passwordRecoveryToken =
            buildPasswordRecoveryToken(user.getId(), tokenHash);

        saveRecoveryToken(passwordRecoveryToken);

        recoveryEmailManager.sendRecoveryEmail(
            user.getInstitutionalEmail(),
            recoveryToken
        );
    }

    private UserDTO getValidUser(
            Optional<UserDTO> optionalUser
    ) throws BusinessException {
        UserDTO user;

        if (optionalUser.isEmpty()) {
            throw new BusinessException(
                "No existe una cuenta registrada con ese correo."
            );
        }

        user = optionalUser.get();

        validateUserIsActive(user);

        return user;
    }

    private void validateUserIsActive(
            UserDTO user
    ) throws BusinessException {
        if (!user.getIsActive()) {
            throw new BusinessException(
                "La cuenta aún no se encuentra activa."
            );
        }
    }

    private void invalidatePreviousTokens(
            int userId
    ) throws BusinessException {
        passwordRecoveryTokenDAO.invalidateTokensByUserId(userId);
    }

    private PasswordRecoveryTokenDTO buildPasswordRecoveryToken(
            int userId,
            String tokenHash
    ) {
        PasswordRecoveryTokenDTO passwordRecoveryToken =
            new PasswordRecoveryTokenDTO();

        passwordRecoveryToken.setUserId(userId);
        passwordRecoveryToken.setTokenHash(tokenHash);
        passwordRecoveryToken.setExpirationDate(getExpirationDate());
        passwordRecoveryToken.setUsed(false);

        return passwordRecoveryToken;
    }

    private Timestamp getExpirationDate() {
        Timestamp expirationDate = Timestamp.from(
            Instant.now().plus(
                RECOVERY_TOKEN_EXPIRATION_HOURS,
                ChronoUnit.HOURS
            )
        );

        return expirationDate;
    }

    private void saveRecoveryToken(
            PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException {
        boolean wasCreated =
            passwordRecoveryTokenDAO.create(passwordRecoveryToken);

        if (!wasCreated) {
            throw new BusinessException(
                "No se pudo generar el token de recuperación."
            );
        }
    }
}