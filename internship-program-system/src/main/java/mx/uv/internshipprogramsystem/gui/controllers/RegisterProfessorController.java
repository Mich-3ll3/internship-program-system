package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;

public class RegisterProfessorController {

    @FXML private TextField textFieldEmail;
    @FXML private PasswordField passwordField;
    @FXML private TextField textFieldName;
    @FXML private TextField textFieldFirstSurname;
    @FXML private TextField textFieldSecondSurname;
    @FXML private TextField textFieldStaffNumber;
    @FXML private CheckBox checkBoxCoordinator;
    @FXML private Button buttonRegister;
    @FXML private Button buttonCancel;

    @FXML
    private void registerProfessor() {
        try {
            InputValidator.validateNotEmpty(textFieldEmail.getText(), "El correo es obligatorio.");
            InputValidator.validateNotEmpty(textFieldName.getText(), "El nombre es obligatorio.");
            InputValidator.validateNotEmpty(textFieldStaffNumber.getText(), "El número de personal es obligatorio.");

            if (textFieldName.getText().length() > 50) {
                throw new BusinessException("El nombre excede el límite de 50 caracteres.");
            }

            UserDTO user = new UserDTO(
                textFieldEmail.getText(),
                passwordField.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true,
                RolUsuario.PROFESOR
            );

            new UserValidator().validateUniqueName(textFieldName.getText());
            int staffNum = Integer.parseInt(textFieldStaffNumber.getText());
            new ProfessorValidator().validateStaffNumber(staffNum);

            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, passwordField.getText());

            ProfessorDTO professor = new ProfessorDTO(
                staffNum,
                checkBoxCoordinator.isSelected(),
                userId,
                textFieldEmail.getText(),
                textFieldName.getText(),
                textFieldFirstSurname.getText(),
                textFieldSecondSurname.getText(),
                true
            );
            
            ProfessorDAO professorDAO = new ProfessorDAO();
            professorDAO.create(professor);

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Profesor registrado correctamente.");
            limpiarCampos();

        } catch (BusinessException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", ex.getMessage());
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Formato", "El número de personal debe ser numérico.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo establecer conexión con la base de datos.");
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
        textFieldStaffNumber.clear();
        checkBoxCoordinator.setSelected(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}