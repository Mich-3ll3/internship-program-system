package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class InternValidator {
    private static final String ENROLLMENT_FORMAT_REGEX = "^zS\\d{8}$";
    public void validateEnrollmentNumber(String enrollmentNumber) throws BusinessException {
        if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) {
            throw new BusinessException("La matrícula no puede estar vacía.");
        }
        
        if(!enrollmentNumber.matches(ENROLLMENT_FORMAT_REGEX)) {
            throw new BusinessException("El formato de la matrícula es inválido.");
        }
    }
}
