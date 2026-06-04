package mx.uv.internshipprogramsystem.logic.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.EducationalExperienceValidator;

public class EducationalExperienceRegistrationManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            EducationalExperienceRegistrationManager.class
        );

    private final EducationalExperienceDAO educationalExperienceDAO;

    public EducationalExperienceRegistrationManager() {
        educationalExperienceDAO = new EducationalExperienceDAO();
    }

    public boolean registerEducationalExperience(
            EducationalExperienceDTO educationalExperience
    ) throws BusinessException {
        EducationalExperienceValidator validator =
            new EducationalExperienceValidator();

        validator.validateForCreation(educationalExperience);

        boolean wasRegistered =
            educationalExperienceDAO.create(educationalExperience);

        if (wasRegistered) {
            LOGGER.info(
                "Caso de uso registrar experiencia educativa completado."
            );
        }

        return wasRegistered;
    }
}