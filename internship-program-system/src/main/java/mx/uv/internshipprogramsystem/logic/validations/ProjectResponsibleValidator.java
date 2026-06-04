package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class ProjectResponsibleValidator {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MIN_POSITION_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MAX_POSITION_LENGTH = 255;

    private static final String PERSON_NAME_PATTERN =
        "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$";

    private static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final String REPEATED_CHARACTER_PATTERN =
        ".*(.)\\1{3,}.*";

    private static final String ABSURD_TEXT_PATTERN =
        ".*(asdf|qwer|zxcv|test|prueba|aaaa|eeee|iiii|oooo|uuuu).*";

    public void validateForRegistration(
            ProjectResponsibleDTO responsible
    ) throws ValidationException {
        InputValidator.validateNotNull(
            responsible,
            "Los datos del responsable no pueden ser nulos."
        );

        validateFirstName(responsible.getFirstName());
        validateLastNameFather(responsible.getLastNameFather());
        validateLastNameMother(responsible.getLastNameMother());
        validateEmail(responsible.getEmail());
        validatePosition(responsible.getPosition());
        validateOrganizationId(responsible.getOrganizationId());
    }

    private void validateFirstName(
            String firstName
    ) throws ValidationException {
        validateRequiredPersonName(
            firstName,
            "El nombre"
        );
    }

    private void validateLastNameFather(
            String lastNameFather
    ) throws ValidationException {
        validateRequiredPersonName(
            lastNameFather,
            "El apellido paterno"
        );
    }

    private void validateLastNameMother(
            String lastNameMother
    ) throws ValidationException {
        if (lastNameMother != null
                && !lastNameMother.trim().isEmpty()) {
            validateOptionalPersonName(
                lastNameMother,
                "El apellido materno"
            );
        }
    }

    private void validateRequiredPersonName(
            String value,
            String fieldName
    ) throws ValidationException {
        InputValidator.validateNotEmpty(
            value,
            fieldName + " no puede estar vacío."
        );

        validatePersonNameLength(
            value,
            fieldName
        );

        validatePersonNameFormat(
            value,
            fieldName
        );

        validateSuspiciousText(
            value,
            fieldName
        );
    }

    private void validateOptionalPersonName(
            String value,
            String fieldName
    ) throws ValidationException {
        validatePersonNameLength(
            value,
            fieldName
        );

        validatePersonNameFormat(
            value,
            fieldName
        );

        validateSuspiciousText(
            value,
            fieldName
        );
    }

    private void validatePersonNameLength(
            String value,
            String fieldName
    ) throws ValidationException {
        if (value.trim().length() < MIN_NAME_LENGTH) {
            throw new ValidationException(
                fieldName + " debe tener al menos "
                + MIN_NAME_LENGTH
                + " caracteres."
            );
        }

        InputValidator.validateMaxLength(
            value,
            MAX_NAME_LENGTH,
            fieldName + " excede el límite permitido."
        );
    }

    private void validatePersonNameFormat(
            String value,
            String fieldName
    ) throws ValidationException {
        if (!value.matches(PERSON_NAME_PATTERN)) {
            throw new ValidationException(
                fieldName
                + " solo puede contener letras, espacios, acentos y ñ."
            );
        }
    }

    private void validateEmail(
            String email
    ) throws ValidationException {
        InputValidator.validateNotEmpty(
            email,
            "El correo electrónico no puede estar vacío."
        );

        InputValidator.validateMaxLength(
            email,
            MAX_EMAIL_LENGTH,
            "El correo electrónico excede el límite permitido."
        );

        validateEmailFormat(
            email
        );
    }

    private void validateEmailFormat(
            String email
    ) throws ValidationException {
        if (!email.matches(EMAIL_PATTERN)) {
            throw new ValidationException(
                "El formato del correo electrónico es inválido."
            );
        }
    }

    private void validatePosition(
            String position
    ) throws ValidationException {
        InputValidator.validateNotEmpty(
            position,
            "El cargo no puede estar vacío."
        );

        validatePositionLength(
            position
        );

        validateSuspiciousText(
            position,
            "El cargo"
        );
    }

    private void validatePositionLength(
            String position
    ) throws ValidationException {
        if (position.trim().length() < MIN_POSITION_LENGTH) {
            throw new ValidationException(
                "El cargo debe tener al menos "
                + MIN_POSITION_LENGTH
                + " caracteres."
            );
        }

        InputValidator.validateMaxLength(
            position,
            MAX_POSITION_LENGTH,
            "El cargo excede el límite permitido."
        );
    }

    private void validateSuspiciousText(
            String value,
            String fieldName
    ) throws ValidationException {
        validateRepeatedCharacters(
            value,
            fieldName
        );

        validateAbsurdPattern(
            value,
            fieldName
        );
    }

    private void validateRepeatedCharacters(
            String value,
            String fieldName
    ) throws ValidationException {
        if (value.toLowerCase().matches(REPEATED_CHARACTER_PATTERN)) {
            throw new ValidationException(
                fieldName
                + " no puede contener caracteres repetidos excesivamente."
            );
        }
    }

    private void validateAbsurdPattern(
            String value,
            String fieldName
    ) throws ValidationException {
        if (value.toLowerCase().matches(ABSURD_TEXT_PATTERN)) {
            throw new ValidationException(
                fieldName + " contiene un texto no válido."
            );
        }
    }

    private void validateOrganizationId(
            int organizationId
    ) throws ValidationException {
        InputValidator.validatePositive(
            organizationId,
            "La organización seleccionada no es válida."
        );
    }
}