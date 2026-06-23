package mx.uv.internshipprogramsystem.logic.managers;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

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
    ) throws BusinessException, DataAccessException {
        EducationalExperienceValidator validator =
            new EducationalExperienceValidator();

        validator.validateForCreation(educationalExperience);

        if (educationalExperience.getStartDate() == null
                || educationalExperience.getEndDate() == null) {
            throw new BusinessException(
                "Debe capturar la fecha de inicio y la fecha de fin."
            );
        }

        if (educationalExperienceDAO.existsSectionByPeriod(
                educationalExperience.getSchoolPeriod(),
                educationalExperience.getSection()
        )) {
            throw new BusinessException(
                "La seccion ya existe en el periodo seleccionado."
            );
        }

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