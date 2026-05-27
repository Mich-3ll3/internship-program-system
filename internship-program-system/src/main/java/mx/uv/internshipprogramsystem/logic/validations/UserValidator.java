package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class UserValidator {
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MAX_SURNAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final String NAME_PATTERN = "^[\\p{L} ]+$";
    private static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@(uv\\.mx|estudiantes\\.uv\\.mx)$";

    public void validateUserForCreation(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        validateInstitutionalEmail(user.getInstitutionalEmail());
        validateRequiredName(user.getName());
        validateRequiredFirstSurname(user.getFirstSurname());
        validateOptionalSecondSurname(user.getSecondSurname());
    }

    public void validateUserForUpdate(UserDTO user) throws BusinessException {
        InputValidator.validateNotNull(user, "UserDTO no puede ser nulo.");
        validateRequiredName(user.getName());
        validateRequiredFirstSurname(user.getFirstSurname());
        validateOptionalSecondSurname(user.getSecondSurname());
    }

    public void validateEmailFormat(String email) throws BusinessException {
        if (email != null && !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException(
                "El correo debe ser institucional (@uv.mx o @estudiantes.uv.mx)."
            );
        }
    }

    private void validateInstitutionalEmail(String email)
            throws ValidationException, BusinessException {
        validateRequiredString(email, "Correo institucional");
        validateMaxLength(email, "Correo institucional", MAX_EMAIL_LENGTH);
        validateEmailFormat(email);
    }

    private void validateRequiredName(String name) throws ValidationException {
        validateRequiredString(name, "Nombre");
        validateMaxLength(name, "Nombre", MAX_NAME_LENGTH);
        validateNameFormat(name, "Nombre");
    }

    private void validateRequiredFirstSurname(String firstSurname) throws ValidationException {
        validateRequiredString(firstSurname, "Apellido paterno");
        validateMaxLength(firstSurname, "Apellido paterno", MAX_SURNAME_LENGTH);
        validateNameFormat(firstSurname, "Apellido paterno");
    }

    private void validateOptionalSecondSurname(String secondSurname) throws ValidationException {
        validateMaxLength(secondSurname, "Apellido materno", MAX_SURNAME_LENGTH);
        validateOptionalNameFormat(secondSurname, "Apellido materno");
    }

    private void validateNameFormat(String value, String fieldName) throws ValidationException {
        if (value != null && !value.matches(NAME_PATTERN)) {
            throw new ValidationException(fieldName + " contiene caracteres no permitidos.");
        }
    }

    private void validateOptionalNameFormat(
            String value,
            String fieldName
    ) throws ValidationException {
        if (value != null && !value.trim().isEmpty()) {
            validateNameFormat(value, fieldName);
        }
    }

    private void validateRequiredString(
            String value,
            String fieldName
    ) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " es un campo obligatorio.");
        }
    }

    private void validateMaxLength(
            String value,
            String fieldName,
            int maxLength
    ) throws ValidationException {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(
                fieldName + " no puede exceder los "
                + maxLength
                + " caracteres."
            );
        }
    }
}