package mx.uv.internshipprogramsystem.logic.managers;

import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ActivationTokenResendManager {
    private final UserDAO userDAO;
    private final ActivationEmailManager activationEmailManager;

    public ActivationTokenResendManager() {
        userDAO = new UserDAO();
        activationEmailManager = new ActivationEmailManager();
    }

    public void resendActivationToken(String institutionalEmail)
            throws BusinessException {
        InputValidator.validateNotEmpty(
            institutionalEmail,
            "El correo institucional no puede estar vacío."
        );

        UserDTO user = userDAO.findByInstitutionalEmail(
            institutionalEmail
        );

        validateUserCanRequestToken(user);

        activationEmailManager.resendActivationToken(
            user.getId(),
            user.getInstitutionalEmail()
        );
    }

    private void validateUserCanRequestToken(UserDTO user)
            throws BusinessException {
        if (user.getIsActive()) {
            throw new BusinessException(
                "La cuenta ya se encuentra activa."
            );
        }
    }
}