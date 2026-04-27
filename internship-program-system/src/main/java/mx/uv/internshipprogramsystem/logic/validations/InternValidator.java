package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternValidator.class);
    private static final String ENROLLMENT_FORMAT_REGEX = "^zS\\d{8}$";
    
    public void validateEnrollmentNumber(String enrollmentNumber) throws BusinessException {
        if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) {
            LOGGER.warn("Validación fallida: matrícula vacía o nula.");
            throw new BusinessException("La matrícula no puede estar vacía.");
        }
        
        if(!enrollmentNumber.matches(ENROLLMENT_FORMAT_REGEX)) {
            LOGGER.warn("Validación fallida: formato inválido para matrícula [{}]", enrollmentNumber);
            throw new BusinessException("El formato de la matrícula es inválido.");
        }
    }
}
