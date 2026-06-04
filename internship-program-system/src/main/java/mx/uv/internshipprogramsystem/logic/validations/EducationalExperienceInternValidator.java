package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class EducationalExperienceInternValidator {
    
    private static final int NRC_LENGTH = 5;

    public void validateForAssignment(
            EducationalExperienceInternDTO assignment
    ) throws ValidationException {
        InputValidator.validateNotNull(
            assignment,
            "La asignación de experiencia educativa no puede ser nula."
        );

        validateNrc(
            assignment.getNrc()
        );

        validateInternId(
            assignment.getInternId()
        );
    }

    private void validateNrc(String nrc)
            throws ValidationException {
        InputValidator.validateNotEmpty(
            nrc,
            "El NRC no puede estar vacío."
        );

        if (!nrc.matches("\\d{" + NRC_LENGTH + "}")) {
            throw new ValidationException(
                "El NRC debe contener exactamente "
                + NRC_LENGTH
                + " dígitos."
            );
        }
    }

    private void validateInternId(int internId)
            throws ValidationException {
        InputValidator.validatePositive(
            internId,
            "El id del estudiante debe ser positivo."
        );
    }
}
