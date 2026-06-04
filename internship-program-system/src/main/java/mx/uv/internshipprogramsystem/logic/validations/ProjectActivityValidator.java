package mx.uv.internshipprogramsystem.logic.validations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public final class ProjectActivityValidator {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_MONTH_LENGTH = 60;
    private static final int MIN_WEEK = 1;
    private static final int MAX_WEEK = 5;
    private static final int MAX_ACTIVITIES = 20;

    private ProjectActivityValidator() {
    }

    public static void validateActivityList(
            List<ProjectActivityDTO> activities
    ) throws ValidationException {
        if (activities == null) {
            throw new ValidationException(
                "La lista de actividades no puede ser nula."
            );
        }

        if (activities.isEmpty()) {
            throw new ValidationException(
                "Debe registrar al menos una actividad."
            );
        }

        if (activities.size() > MAX_ACTIVITIES) {
            throw new ValidationException(
                "No se pueden registrar más de 20 actividades."
            );
        }

        validateActivities(activities);
        validateDuplicatedActivities(activities);
    }

    public static void validateActivity(
            ProjectActivityDTO activity
    ) throws ValidationException {
        if (activity == null) {
            throw new ValidationException(
                "La actividad no puede ser nula."
            );
        }

        validateRequiredText(
            activity.getName(),
            "El nombre de la actividad es obligatorio."
        );

        validateRequiredText(
            activity.getMonth(),
            "El mes de la actividad es obligatorio."
        );

        validateMaximumLength(
            activity.getName(),
            MAX_NAME_LENGTH,
            "El nombre de la actividad no debe superar 100 caracteres."
        );

        validateMaximumLength(
            activity.getMonth(),
            MAX_MONTH_LENGTH,
            "El mes de la actividad no debe superar 60 caracteres."
        );

        validateWeekRange(activity);
        validateSafeText(activity.getName(), "nombre de la actividad");
        validateSafeText(activity.getMonth(), "mes de la actividad");
    }

    private static void validateActivities(
            List<ProjectActivityDTO> activities
    ) throws ValidationException {
        for (ProjectActivityDTO activity : activities) {
            validateActivity(activity);
        }
    }

    private static void validateWeekRange(
            ProjectActivityDTO activity
    ) throws ValidationException {
        if (activity.getStartWeek() == null) {
            throw new ValidationException(
                "La semana de inicio es obligatoria."
            );
        }

        if (activity.getEndWeek() == null) {
            throw new ValidationException(
                "La semana de fin es obligatoria."
            );
        }

        if (activity.getStartWeek() < MIN_WEEK
                || activity.getStartWeek() > MAX_WEEK) {
            throw new ValidationException(
                "La semana de inicio debe estar entre 1 y 5."
            );
        }

        if (activity.getEndWeek() < MIN_WEEK
                || activity.getEndWeek() > MAX_WEEK) {
            throw new ValidationException(
                "La semana de fin debe estar entre 1 y 5."
            );
        }

        if (activity.getStartWeek() > activity.getEndWeek()) {
            throw new ValidationException(
                "La semana de inicio no puede ser mayor que la semana de fin."
            );
        }
    }

    private static void validateDuplicatedActivities(
            List<ProjectActivityDTO> activities
    ) throws ValidationException {
        Set<String> activityKeys = new HashSet<>();

        for (ProjectActivityDTO activity : activities) {
            String key = activity.getName().trim().toLowerCase()
                    + "|"
                    + activity.getMonth().trim().toLowerCase();

            if (activityKeys.contains(key)) {
                throw new ValidationException(
                    "No se permiten actividades duplicadas en el mismo mes."
                );
            }

            activityKeys.add(key);
        }
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

    private static void validateSafeText(
            String text,
            String fieldName
    ) throws ValidationException {
        if (text != null
                && text.matches(".*[\\p{Cntrl}&&[^\r\n\t]].*")) {
            throw new ValidationException(
                "El campo " + fieldName
                    + " contiene caracteres no permitidos."
            );
        }
    }
}