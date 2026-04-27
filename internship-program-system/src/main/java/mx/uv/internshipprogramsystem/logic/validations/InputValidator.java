package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class InputValidator {
    public static void validateNotNull(Object object, String message) throws ValidationException {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    public static void validateNotEmpty(String value, String message) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    public static void validatePositive(int number, String message) throws ValidationException {
        if (number <= 0) {
            throw new ValidationException(message);
        }
    }
}
