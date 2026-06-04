package mx.uv.internshipprogramsystem.logic.validations;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public final class ProjectScheduleValidator {
    private static final int MAX_SCHEDULES = 5;
    private static final long MAX_DAILY_HOURS = 8;
    private static final long MAX_WEEKLY_HOURS = 40;
    private static final long RECOMMENDED_WEEKLY_HOURS = 20;

    private ProjectScheduleValidator() {
    }

    public static void validateScheduleList(
            List<ProjectScheduleDTO> schedules
    ) throws ValidationException {
        if (schedules == null) {
            throw new ValidationException(
                "La lista de horarios no puede ser nula."
            );
        }

        if (schedules.isEmpty()) {
            throw new ValidationException(
                "Debe registrar al menos un horario."
            );
        }

        if (schedules.size() > MAX_SCHEDULES) {
            throw new ValidationException(
                "No se pueden registrar más de 5 días de horario."
            );
        }

        validateSchedules(schedules);
        validateDuplicatedDays(schedules);
        validateWeeklyHours(schedules);
    }

    public static void validateSchedule(
            ProjectScheduleDTO schedule
    ) throws ValidationException {
        if (schedule == null) {
            throw new ValidationException(
                "El horario no puede ser nulo."
            );
        }

        validateWeekDay(schedule.getWeekDay());
        validateTimes(schedule.getEntryTime(), schedule.getExitTime());
    }

    private static void validateSchedules(
            List<ProjectScheduleDTO> schedules
    ) throws ValidationException {
        for (ProjectScheduleDTO schedule : schedules) {
            validateSchedule(schedule);
        }
    }

    private static void validateWeekDay(String weekDay)
            throws ValidationException {
        if (weekDay == null || weekDay.trim().isEmpty()) {
            throw new ValidationException(
                "El día del horario es obligatorio."
            );
        }

        if (!isAllowedWeekDay(weekDay.trim())) {
            throw new ValidationException(
                "El día del horario solo puede ser de lunes a viernes."
            );
        }
    }

    private static boolean isAllowedWeekDay(String weekDay) {
        boolean isAllowed = false;

        if ("LUNES".equals(weekDay)
                || "MARTES".equals(weekDay)
                || "MIERCOLES".equals(weekDay)
                || "JUEVES".equals(weekDay)
                || "VIERNES".equals(weekDay)) {
            isAllowed = true;
        }

        return isAllowed;
    }

    private static void validateTimes(
            LocalTime entryTime,
            LocalTime exitTime
    ) throws ValidationException {
        if (entryTime == null) {
            throw new ValidationException(
                "La hora de entrada es obligatoria."
            );
        }

        if (exitTime == null) {
            throw new ValidationException(
                "La hora de salida es obligatoria."
            );
        }

        if (!entryTime.isBefore(exitTime)) {
            throw new ValidationException(
                "La hora de entrada debe ser menor que la hora de salida."
            );
        }

        validateDailyHours(entryTime, exitTime);
    }

    private static void validateDailyHours(
            LocalTime entryTime,
            LocalTime exitTime
    ) throws ValidationException {
        long dailyHours = Duration.between(
            entryTime,
            exitTime
        ).toHours();

        if (dailyHours <= 0) {
            throw new ValidationException(
                "El horario debe cubrir al menos una hora."
            );
        }

        if (dailyHours > MAX_DAILY_HOURS) {
            throw new ValidationException(
                "No se permiten más de 8 horas por día."
            );
        }
    }

    private static void validateDuplicatedDays(
            List<ProjectScheduleDTO> schedules
    ) throws ValidationException {
        Set<String> days = new HashSet<>();

        for (ProjectScheduleDTO schedule : schedules) {
            String day = schedule.getWeekDay().trim().toUpperCase();

            if (days.contains(day)) {
                throw new ValidationException(
                    "No se puede repetir el mismo día en el horario."
                );
            }

            days.add(day);
        }
    }

    private static void validateWeeklyHours(
            List<ProjectScheduleDTO> schedules
    ) throws ValidationException {
        long weeklyHours = 0;

        for (ProjectScheduleDTO schedule : schedules) {
            weeklyHours += Duration.between(
                schedule.getEntryTime(),
                schedule.getExitTime()
            ).toHours();
        }

        if (weeklyHours > MAX_WEEKLY_HOURS) {
            throw new ValidationException(
                "El horario no puede superar 40 horas semanales."
            );
        }

        if (weeklyHours < RECOMMENDED_WEEKLY_HOURS) {
            throw new ValidationException(
                "El horario debe cubrir al menos 20 horas semanales."
            );
        }
    }
}