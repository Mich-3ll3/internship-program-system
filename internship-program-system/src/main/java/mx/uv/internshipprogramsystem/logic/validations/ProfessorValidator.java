package mx.uv.internshipprogramsystem.logic.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class ProfessorValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorValidator.class);
    
    private static final int MAX_STAFF_NUMBER_LENGTH = 6;
    private static final String STAFF_NUMBER_REGEX = "^\\d{6}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._-]{3,40}@uv\\.mx$";
    private static final String NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,40}$";
    
    private static final String DOUBLE_SPACE_REGEX = ".*\\s{2,}.*";
    private static final String REPEATED_CHARS_REGEX = ".*(.)\\1{3,}.*";
    private static final String VOWEL_REGEX = ".*[aeiouAEIOUáéíóúÁÉÍÓÚ].*";

    public void validateProfessorForCreation(ProfessorDTO professor) throws ValidationException {
        InputValidator.validateNotNull(professor, "Los datos del profesor son nulos.");
        
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        try {
            InputValidator.validatePositive(
                professor.getId(),
                "El identificador de la cuenta no es válido."
            );
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validateStaffNumber(professor.getStaffNumber());
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validateInstitutionalEmail(professor.getInstitutionalEmail());
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }
        
        try {
            validateName(professor.getName(), "nombre(s)");
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validateName(professor.getFirstSurname(), "apellido paterno");
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        if (professor.getSecondSurname() != null && !professor.getSecondSurname().isBlank()) {
            try {
                validateName(professor.getSecondSurname(), "apellido materno");
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public void validateStaffNumber(String staffNumber) throws ValidationException {
        InputValidator.validateNotEmpty(staffNumber, "El número de personal está vacío.");
        InputValidator.validateMaxLength(
            staffNumber,
            MAX_STAFF_NUMBER_LENGTH,
            "El número de personal excede el límite permitido."
        );

        if (!staffNumber.matches(STAFF_NUMBER_REGEX)) {
            LOGGER.warn("Violación de formato en número de personal: {}", staffNumber);
            throw new ValidationException("El formato del número de personal es inválido.");
        }
    }

    public void validateInstitutionalEmail(String email) throws ValidationException {
        InputValidator.validateNotEmpty(email, "El correo institucional está vacío.");

        if (!email.matches(EMAIL_REGEX)) {
            LOGGER.warn("Violación de dominio o longitud en correo: {}", email);
            throw new ValidationException(
                "El formato del correo institucional es inválido."
            );
        }
    }

    private void validateName(String name, String fieldLabel) throws ValidationException {
        InputValidator.validateNotEmpty(name, "El campo " + fieldLabel + " está vacío.");

        String cleanName = name.trim();

        if (!cleanName.matches(NAME_REGEX)) {
            LOGGER.warn("Violación de caracteres o longitud en {}: {}", fieldLabel, cleanName);
            throw new ValidationException(
                "El campo " + fieldLabel + " solo acepta letras (2 a 40 caracteres)."
            );
        }

        if (cleanName.matches(DOUBLE_SPACE_REGEX)) {
            LOGGER.warn("Violación de espacios dobles en {}: {}", fieldLabel, cleanName);
            throw new ValidationException(
                "El campo " + fieldLabel + " no debe contener espacios dobles."
            );
        }

        if (cleanName.matches(REPEATED_CHARS_REGEX)) {
            LOGGER.warn("Violación de caracteres repetidos en {}: {}", fieldLabel, cleanName);
            throw new ValidationException(
                "El campo " + fieldLabel + " contiene demasiados caracteres repetidos consecutivos."
            );
        }

        if (!cleanName.matches(VOWEL_REGEX)) {
            LOGGER.warn("Violación de ausencia de vocales en {}: {}", fieldLabel, cleanName);
            throw new ValidationException(
                "El campo " + fieldLabel + " no parece ser una palabra válida."
            );
        }
    }

    public void validateProfessorForUpdate(ProfessorDTO professor) throws ValidationException {
        InputValidator.validateNotNull(professor, "Los datos del profesor son nulos.");
        
        java.util.List<String> errors = new java.util.ArrayList<>();

        try {
            validateName(professor.getName(), "nombre(s)");
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validateName(professor.getFirstSurname(), "apellido paterno");
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        if (professor.getSecondSurname() != null && !professor.getSecondSurname().isBlank()) {
            try {
                validateName(professor.getSecondSurname(), "apellido materno");
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}