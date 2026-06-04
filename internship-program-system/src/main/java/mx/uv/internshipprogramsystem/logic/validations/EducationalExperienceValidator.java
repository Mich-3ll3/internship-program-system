package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class EducationalExperienceValidator {
    
    private static final int NRC_LENGTH = 5;
    private static final int SCHOOL_PERIOD_LENGTH = 7;
    private static final int MAX_SECTION_LENGTH = 2;

    private static final String SCHOOL_PERIOD_PATTERN = "^\\d{4}-(51|01)$";
    private static final String SECTION_PATTERN = "^\\d{1,2}$";

    public void validateForCreation(
            EducationalExperienceDTO educationalExperience
    ) throws ValidationException {
        InputValidator.validateNotNull(
            educationalExperience,
            "La experiencia educativa no puede ser nula."
        );

        validateNrc(educationalExperience.getNrc());
        validateSchoolPeriod(educationalExperience.getSchoolPeriod());
        validateSection(educationalExperience.getSection());
        validateProfessorId(educationalExperience.getProfessorId());
    }

    public void validateNrc(String nrc) throws ValidationException {
        InputValidator.validateNotEmpty(nrc, "El NRC no puede estar vacío.");

        if (!nrc.matches("\\d{" + NRC_LENGTH + "}")) {
            throw new ValidationException(
                "El NRC debe contener exactamente "
                + NRC_LENGTH
                + " dígitos."
            );
        }
    }

    private void validateSchoolPeriod(String schoolPeriod) throws ValidationException {
        InputValidator.validateNotEmpty(
            schoolPeriod,
            "El periodo escolar no puede estar vacío."
        );
        
        validateSchoolPeriodLength(schoolPeriod);
        
        validateSchoolPeriodFormat(schoolPeriod);
    }
    
    private void validateSchoolPeriodLength(String schoolPeriod) throws ValidationException {
        if (schoolPeriod.length() != SCHOOL_PERIOD_LENGTH) {
            throw new ValidationException(
                "El periodo escolar debe tener exactamente "
                + SCHOOL_PERIOD_LENGTH
                + " caracteres."
            );
        }
    }
    
    private void validateSchoolPeriodFormat(String schoolPeriod) throws ValidationException {
        if (!schoolPeriod.matches(SCHOOL_PERIOD_PATTERN)) {
            throw new ValidationException(
                "El periodo escolar debe tener el formato YYYY-51 o YYYY-01."
            );
        }
    }

    private void validateSection(String section) throws ValidationException {
        InputValidator.validateNotEmpty(
            section,
            "La sección no puede estar vacía."
        );
        
        validateSectionLength(section);
        
        validateSectionFormat(section);
    }
    
    private void validateSectionLength(String section) throws ValidationException {
        if (section.length() > MAX_SECTION_LENGTH) {
            throw new ValidationException(
                "La sección no puede exceder "
                + MAX_SECTION_LENGTH
                + " caracteres."
            );
        }
    }
    
    private void validateSectionFormat(String section) throws ValidationException {
        if (!section.matches(SECTION_PATTERN)) {
            throw new ValidationException(
                "La sección debe ser numérica, por ejemplo 1, 2 o 3."
            );
        }
    }

    private void validateProfessorId(int professorId) throws ValidationException {
        InputValidator.validatePositive(
            professorId,
            "El id del profesor debe ser positivo."
        );
    }
}