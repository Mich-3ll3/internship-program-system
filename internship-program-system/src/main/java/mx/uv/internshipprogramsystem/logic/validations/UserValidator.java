package mx.uv.internshipprogramsystem.logic.validations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class UserValidator {
    private static final int MAX_NAME_LENGTH = 60;
    private static final int MAX_SURNAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(uv\\.mx|estudiantes\\.uv\\.mx)$";
    
    public void validateUserForCreation(UserDTO user, String password) throws BusinessException, ValidationException {
        checkString(user.getInstitucionalEmail(), "Correo institucional", MAX_EMAIL_LENGTH, true);
        checkString(user.getName(), "Nombre", MAX_NAME_LENGTH, true);
        checkString(user.getFirstSurname(), "Apellido paterno", MAX_SURNAME_LENGTH, true);
        checkString(user.getSecondSurname(), "Apellido materno", MAX_SURNAME_LENGTH, false);
        validateEmailFormat(user.getInstitucionalEmail());
        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.validatePassword(password);
        validateUniqueName(user.getName());
    }
    
    public void validateUserForUpdate(UserDTO user) throws ValidationException, BusinessException {
        checkString(user.getName(), "Nombre", MAX_NAME_LENGTH, true);
        checkString(user.getFirstSurname(), "Apellido paterno", MAX_SURNAME_LENGTH, true);
        checkString(user.getSecondSurname(), "Apellido materno", MAX_SURNAME_LENGTH, false);
        if (user.getInstitucionalEmail() == null || user.getInstitucionalEmail().trim().isEmpty()) {
            throw new ValidationException("El correo institucional no puede ser nulo o vacío.");
        }
    }
    
    public void validateUniqueName(String name) throws BusinessException {
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM USUARIO WHERE nombre = ?")) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new ValidationException("Ya existe un usuario con el nombre: " + name);
                }
            }
        } catch (SQLException exception) {
            throw new BusinessException("Error validando nombre único", exception);
        }
    }

    public void validateEmailFormat(String email) throws BusinessException {
        if (email != null && !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException("El correo debe ser institucional (@uv.mx o @estudiantes.uv.mx).");
        }
    }

    private void checkString(String value, String fieldName, int maxLength, boolean isMandatory) throws ValidationException {
        if (isMandatory && (value == null || value.trim().isEmpty())) {
            throw new ValidationException(fieldName + " es un campo obligatorio.");
        }
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName + " no puede exceder los " + maxLength + " caracteres.");
        }
    }
}
