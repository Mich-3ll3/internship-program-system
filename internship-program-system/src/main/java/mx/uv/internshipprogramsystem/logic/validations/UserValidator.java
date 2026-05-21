package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class UserValidator {
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MAX_SURNAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final String EMAIL_PATTERN =
    "^[A-Za-z0-9+_.-]+@(uv\\.mx|estudiantes\\.uv\\.mx)$";

    public void validateUserForCreation(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        checkString(user.getInstitutionalEmail(),"Correo institucional", MAX_EMAIL_LENGTH,true);
        checkString(user.getName(), "Nombre", MAX_NAME_LENGTH, true);
        checkString(user.getFirstSurname(),"Apellido paterno", MAX_SURNAME_LENGTH,true);
        checkString(user.getSecondSurname(),"Apellido materno", MAX_SURNAME_LENGTH,false);
        validateEmailFormat(user.getInstitutionalEmail());
    }

    public void validateUserForUpdate(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        checkString(user.getName(), "Nombre", MAX_NAME_LENGTH, true);
        checkString(user.getFirstSurname(),"Apellido paterno", MAX_SURNAME_LENGTH,true);
        checkString(user.getSecondSurname(),"Apellido materno", MAX_SURNAME_LENGTH, false);
        InputValidator.validateNotEmpty(
            user.getInstitutionalEmail(),
            "El correo institucional no puede ser nulo o vacío."
        );
        validateEmailFormat(user.getInstitutionalEmail());
    }

    public void validateEmailFormat(String email) throws BusinessException {
        if (email != null && !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException(
                "El correo debe ser institucional (@uv.mx o @estudiantes.uv.mx)."
            );
        }
    }

    private void checkString(
            String value,
            String fieldName,
            int maxLength,
            boolean isMandatory
    ) throws ValidationException {
        if (isMandatory && (value == null || value.trim().isEmpty())) {
            throw new ValidationException(fieldName + " es un campo obligatorio.");
        }

        if (value != null && value.length() > maxLength) {
            throw new ValidationException(
                fieldName + " no puede exceder los "
                    + maxLength
                    + " caracteres."
            );
        }
    }
}