package mx.uv.internshipprogramsystem.logic.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceInternDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.EducationalExperienceInternValidator;

public class EducationalExperienceInternAssignmentManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceInternAssignmentManager.class
        );

    private final EducationalExperienceInternDAO
            educationalExperienceInternDAO;

    private final EducationalExperienceInternValidator
            educationalExperienceInternValidator;

    public EducationalExperienceInternAssignmentManager() {
        educationalExperienceInternDAO =
            new EducationalExperienceInternDAO();

        educationalExperienceInternValidator =
            new EducationalExperienceInternValidator();
    }

    public boolean assignInternToEducationalExperience(
            EducationalExperienceInternDTO assignment
    ) throws BusinessException {
        educationalExperienceInternValidator.validateForAssignment(
            assignment
        );

        validateActiveEducationalExperience(
            assignment.getNrc()
        );

        validateRepeatedAssignment(
            assignment
        );

        validateInternActiveAssignment(
            assignment.getInternId()
        );

        boolean wasAssigned =
            educationalExperienceInternDAO.create(
                assignment
            );

        if (wasAssigned) {
            LOGGER.info(
                "Caso de uso asignar experiencia educativa completado."
            );
        }

        return wasAssigned;
    }

    private void validateActiveEducationalExperience(
            String nrc
    ) throws BusinessException {
        boolean existsActiveExperience =
            educationalExperienceInternDAO
                .existsActiveEducationalExperienceByNrc(
                    nrc
                );

        if (!existsActiveExperience) {
            LOGGER.warn(
                "Intento de asignación a experiencia educativa inactiva."
            );

            throw new BusinessException(
                "La experiencia educativa seleccionada no está activa."
            );
        }
    }

    private void validateRepeatedAssignment(
            EducationalExperienceInternDTO assignment
    ) throws BusinessException {
        boolean existsAssignment =
            educationalExperienceInternDAO.existsAssignment(
                assignment
            );

        if (existsAssignment) {
            LOGGER.warn(
                "Intento de asignación duplicada a experiencia educativa."
            );

            throw new BusinessException(
                "El estudiante ya está asignado a esta experiencia educativa."
            );
        }
    }

    private void validateInternActiveAssignment(
            int internId
    ) throws BusinessException {
        boolean hasActiveAssignment =
            educationalExperienceInternDAO
                .existsActiveAssignmentByInternId(
                    internId
                );

        if (hasActiveAssignment) {
            LOGGER.warn(
                "Intento de asignar estudiante con experiencia activa."
            );

            throw new BusinessException(
                "El estudiante ya tiene una experiencia educativa activa."
            );
        }
    }
}