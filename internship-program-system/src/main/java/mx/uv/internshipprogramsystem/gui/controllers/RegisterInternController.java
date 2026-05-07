package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;

public class RegisterInternController {

    @FXML private TextField textFieldEmail;
    @FXML private PasswordField passwordField;
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldFirstSurname;
    @FXML private TextField textFieldSecondSurname;
    @FXML private TextField textFieldEnrollment;
    @FXML private Button buttonRegister;
    @FXML private Button buttonCancel;

    @FXML
    private void handleRegister() {
        try {
            InputValidator.validateNotEmpty(textFieldEmail.getText(), "El correo es obligatorio.");
            InputValidator.validateNotEmpty(passwordField.getText(), "La contraseña es obligatoria.");
            InputValidator.validateNotEmpty(textFieldName.getText(), "El nombre es obligatorio.");
            InputValidator.validateNotEmpty(textFieldEnrollment.getText(), "La matrícula es obligatoria.");

            if (textFieldName.getText().length() > 50) {
                throw new BusinessException("El nombre excede el límite permitido.");
            }

            UserDTO user = new UserDTO(
                textFieldEmail.getText(),
                passwordField.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true,
                RolUsuario.ESTUDIANTE
            );
            
            new UserValidator().validateUniqueName(textFieldName.getText());
            new InternValidator().validateEnrollmentNumber(textFieldEnrollment.getText());

            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, passwordField.getText());

            InternDTO intern = new InternDTO(
                textFieldEnrollment.getText(),
                userId,
                user.getInstitucionalEmail(),
                user.getName(),
                user.getFirstSurname(),
                user.getSecondSurname(),
                user.getIsActive(),
                user.getRol()
            );

            InternDAO internDAO = new InternDAO();
            internDAO.create(intern);

            showAlert(Alert.AlertType.INFORMATION, "Registro exitoso", "El estudiante fue registrado correctamente.");
            limpiarCampos();

        } catch (BusinessException ex) {
            showAlert(Alert.AlertType.WARNING, "Error de validación", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        textFieldEmail.getScene().getWindow().hide();
    }

    private void limpiarCampos() {
        textFieldEmail.clear();
        passwordField.clear();
        textFieldName.clear();
        textFieldFirstSurname.clear();
        textFieldSecondSurname.clear();
        textFieldEnrollment.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
