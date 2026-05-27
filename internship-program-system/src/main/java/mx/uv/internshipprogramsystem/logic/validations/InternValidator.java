package mx.uv.internshipprogramsystem.logic.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
public class InternValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternValidator.class);
    private static final int MAX_ENROLLMENT_LENGTH = 10;
    private static final String ENROLLMENT_FORMAT_REGEX = "^zS\\d{8}$";

    public void validateInternForCreation(InternDTO intern) throws BusinessException {
        InputValidator.validateNotNull(intern, "Los datos del estudiante no pueden ser nulos.");
        validateEnrollmentNumber(intern.getEnrollmentNumber());
        InputValidator.validatePositive(
            intern.getId(),
            "El identificador del usuario no es válido."
        );
    }
    
    public void validateEnrollmentNumber(String enrollmentNumber) throws BusinessException {
        InputValidator.validateNotEmpty(enrollmentNumber, "La matrícula no puede estar vacía.");
        InputValidator.validateMaxLength(
            enrollmentNumber,
            MAX_ENROLLMENT_LENGTH,
            "La matrícula excede el límite permitido."
        );

        if (!enrollmentNumber.matches(ENROLLMENT_FORMAT_REGEX)) {
            LOGGER.warn("Validación fallida: formato inválido de matrícula.");
            throw new BusinessException("El formato de la matrícula es inválido.");
        }
    }
}
