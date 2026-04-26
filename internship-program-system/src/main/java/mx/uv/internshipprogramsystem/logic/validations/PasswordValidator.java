package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class PasswordValidator {
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$";
    
    public void validatePassword(String plainPassword) throws ValidationException {
        
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new ValidationException("La contraseña es obligatoria y no puede estar vacía.");
        }
        
        if (!plainPassword.matches(PASSWORD_PATTERN)) {
            throw new ValidationException(
                "La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula, " +
                "un número y un carácter especial (@#$%^&+=!)."
            );
        }
    }
}