package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public final class ProjectValidator {
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_METHODOLOGY_LENGTH = 100;
    private static final int MAX_RESPONSIBILITIES_LENGTH = 255;
    private static final int MAX_TEXT_LENGTH = 5000;
    private static final int MIN_DURATION = 1;
    private static final int MAX_DURATION = 60;

    private ProjectValidator() {
    }

    public static void validateForCreate(ProjectDTO project)
            throws ValidationException {
        validateProjectNotNull(project);
        validateProjectRequiredData(project);
        validateProjectLengths(project);
        validateProjectNumbers(project);
        validateProjectTextSecurity(project);
        validateProjectStatus(project);
    }

    public static void validateForUpdate(ProjectDTO project)
            throws ValidationException {
        validateProjectNotNull(project);
        validateProjectId(project.getId());
        validateProjectRequiredData(project);
        validateProjectLengths(project);
        validateProjectNumbers(project);
        validateProjectTextSecurity(project);
        validateProjectStatus(project);
    }

    public static void validateProjectId(Integer id)
            throws ValidationException {
        if (id == null) {
            throw new ValidationException(
                "El identificador del proyecto es obligatorio."
            );
        }

        if (id <= 0) {
            throw new ValidationException(
                "El identificador del proyecto debe ser positivo."
            );
        }
    }

    private static void validateProjectNotNull(ProjectDTO project)
            throws ValidationException {
        if (project == null) {
            throw new ValidationException(
                "El proyecto no puede ser nulo."
            );
        }
    }

    private static void validateProjectRequiredData(ProjectDTO project)
            throws ValidationException {
        validateRequiredText(
            project.getName(),
            "El nombre del proyecto es obligatorio."
        );

        validateRequiredText(
            project.getGeneralDescription(),
            "La descripción general es obligatoria."
        );

        validateRequiredText(
            project.getGeneralObjective(),
            "El objetivo general es obligatorio."
        );

        validateRequiredText(
            project.getImmediateObjectives(),
            "Los objetivos inmediatos son obligatorios."
        );

        validateRequiredText(
            project.getMediateObjective(),
            "Los objetivos mediatos son obligatorios."
        );

        validateRequiredText(
            project.getMethodology(),
            "La metodología es obligatoria."
        );

        validateRequiredText(
            project.getResources(),
            "Los recursos son obligatorios."
        );

        validateRequiredText(
            project.getResponsibilities(),
            "Las responsabilidades son obligatorias."
        );
    }

    private static void validateProjectLengths(ProjectDTO project)
            throws ValidationException {
        validateMaximumLength(
            project.getName(),
            MAX_NAME_LENGTH,
            "El nombre del proyecto no debe superar 255 caracteres."
        );

        validateMaximumLength(
            project.getGeneralDescription(),
            MAX_TEXT_LENGTH,
            "La descripción general es demasiado extensa."
        );

        validateMaximumLength(
            project.getGeneralObjective(),
            MAX_TEXT_LENGTH,
            "El objetivo general es demasiado extenso."
        );

        validateMaximumLength(
            project.getImmediateObjectives(),
            MAX_TEXT_LENGTH,
            "Los objetivos inmediatos son demasiado extensos."
        );

        validateMaximumLength(
            project.getMediateObjective(),
            MAX_TEXT_LENGTH,
            "Los objetivos mediatos son demasiado extensos."
        );

        validateMaximumLength(
            project.getMethodology(),
            MAX_METHODOLOGY_LENGTH,
            "La metodología no debe superar 100 caracteres."
        );

        validateMaximumLength(
            project.getResources(),
            MAX_TEXT_LENGTH,
            "Los recursos son demasiado extensos."
        );

        validateMaximumLength(
            project.getResponsibilities(),
            MAX_RESPONSIBILITIES_LENGTH,
            "Las responsabilidades no deben superar 255 caracteres."
        );
    }

    private static void validateProjectNumbers(ProjectDTO project)
            throws ValidationException {
        validateDuration(project.getDuration());

        validatePositiveInteger(
            project.getLinkedOrganizationId(),
            "La organización vinculada es obligatoria."
        );

        validatePositiveInteger(
            project.getProjectResponsibleId(),
            "El responsable del proyecto es obligatorio."
        );
    }

    private static void validateDuration(Integer duration)
            throws ValidationException {
        if (duration == null) {
            throw new ValidationException(
                "La duración del proyecto es obligatoria."
            );
        }

        if (duration < MIN_DURATION || duration > MAX_DURATION) {
            throw new ValidationException(
                "La duración debe estar entre 1 y 60 meses."
            );
        }
    }

    private static void validateProjectStatus(ProjectDTO project)
            throws ValidationException {
        if (project.getIsActive() == null) {
            throw new ValidationException(
                "El estado del proyecto es obligatorio."
            );
        }
    }

    private static void validateProjectTextSecurity(ProjectDTO project)
            throws ValidationException {
        validateSafeText(project.getName(), "nombre");
        validateSafeText(project.getGeneralDescription(), "descripción");
        validateSafeText(project.getGeneralObjective(), "objetivo general");
        validateSafeText(project.getImmediateObjectives(), "objetivos inmediatos");
        validateSafeText(project.getMediateObjective(), "objetivos mediatos");
        validateSafeText(project.getMethodology(), "metodología");
        validateSafeText(project.getResources(), "recursos");
        validateSafeText(project.getResponsibilities(), "responsabilidades");
    }

    private static void validateRequiredText(
            String text,
            String message
    ) throws ValidationException {
        if (text == null || text.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    private static void validateMaximumLength(
            String text,
            int maximumLength,
            String message
    ) throws ValidationException {
        if (text != null && text.trim().length() > maximumLength) {
            throw new ValidationException(message);
        }
    }

    private static void validatePositiveInteger(
            Integer number,
            String message
    ) throws ValidationException {
        if (number == null || number <= 0) {
            throw new ValidationException(message);
        }
    }

    private static void validateSafeText(
            String text,
            String fieldName
    ) throws ValidationException {
        if (containsControlCharacters(text)) {
            throw new ValidationException(
                "El campo " + fieldName
                    + " contiene caracteres no permitidos."
            );
        }
    }

    private static boolean containsControlCharacters(String text) {
        boolean containsControlCharacters = false;

        if (text != null) {
            containsControlCharacters =
                text.matches(".*[\\p{Cntrl}&&[^\r\n\t]].*");
        }

        return containsControlCharacters;
    }
}