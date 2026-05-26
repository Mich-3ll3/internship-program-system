package mx.uv.internshipprogramsystem.logic.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class ProfessorValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorValidator.class);
    private static final int MAX_STAFF_NUMBER_LENGTH = 6;
    private static final String STAFF_NUMBER_FORMAT_REGEX = "^\\d{6}$";

    public void validateProfessorForCreation(ProfessorDTO professor) throws BusinessException {
        InputValidator.validateNotNull(professor, "Los datos del profesor no pueden ser nulos.");
        validateStaffNumber(professor.getStaffNumber());
        InputValidator.validatePositive(
            professor.getId(),
            "El identificador del usuario no es válido."
        );
    }
    
    public void validateStaffNumber(String staffNumber) throws BusinessException {
        InputValidator.validateNotEmpty(staffNumber, "El número de personal no puede estar vacío.");
        InputValidator.validateMaxLength(
            staffNumber,
            MAX_STAFF_NUMBER_LENGTH,
            "El número de personal excede el límite permitido"
        );

        if (!staffNumber.matches(STAFF_NUMBER_FORMAT_REGEX)) {
            LOGGER.warn("Validación fallida: formato inválido de número de personal.");
            throw new BusinessException("El formato del número de personal es invalido.");
        }
    }
}
