package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class LinkedOrganizationManagementController {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_PATTERN = "^\\d{10}$";

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtState;

    @FXML
    private TextField txtSector;

    @FXML
    private TextField txtDirectUserCount;

    @FXML
    private TextField txtIndirectUserCount;

    @FXML
    private TextField txtSearchName;

    @FXML
    private void validateOrganizationForm() {
        try {
            validateRequiredFields();
            validateEmail();
            validatePhoneNumber();
            validateUserCounts();
            FormAlertSupport.showInformation("Validación exitosa", "Los datos de la organización cumplen con el formato requerido.");
        } catch (ValidationException exception) {
            FormAlertSupport.showWarning("Error de validación", exception.getMessage());
        } catch (NumberFormatException exception) {
            FormAlertSupport.showWarning("Error de validación", "Los usuarios directos e indirectos deben ser numéricos.");
        }
    }

    @FXML
    private void validateSearchOrganization() {
        try {
            InputValidator.validateNotEmpty(txtSearchName.getText(), "Ingrese el nombre de la organización para buscar.");
            FormAlertSupport.showInformation("Búsqueda válida", "El nombre de organización puede consultarse.");
        } catch (ValidationException exception) {
            FormAlertSupport.showWarning("Error de validación", exception.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtPhoneNumber.clear();
        txtAddress.clear();
        txtCity.clear();
        txtState.clear();
        txtSector.clear();
        txtDirectUserCount.clear();
        txtIndirectUserCount.clear();
    }

    private void validateRequiredFields() throws ValidationException {
        InputValidator.validateNotEmpty(txtName.getText(), "El nombre es obligatorio.");
        InputValidator.validateNotEmpty(txtEmail.getText(), "El correo es obligatorio.");
        InputValidator.validateNotEmpty(txtPhoneNumber.getText(), "El teléfono es obligatorio.");
        InputValidator.validateNotEmpty(txtAddress.getText(), "La dirección es obligatoria.");
        InputValidator.validateNotEmpty(txtCity.getText(), "La ciudad es obligatoria.");
        InputValidator.validateNotEmpty(txtState.getText(), "El estado es obligatorio.");
        InputValidator.validateNotEmpty(txtSector.getText(), "El sector es obligatorio.");
    }

    private void validateEmail() throws ValidationException {
        if (!txtEmail.getText().matches(EMAIL_PATTERN)) {
            throw new ValidationException("El correo de la organización no tiene un formato válido.");
        }
    }

    private void validatePhoneNumber() throws ValidationException {
        if (!txtPhoneNumber.getText().matches(PHONE_PATTERN)) {
            throw new ValidationException("El teléfono debe contener 10 dígitos.");
        }
    }

    private void validateUserCounts() throws ValidationException {
        validatePositiveNumber(txtDirectUserCount.getText(), "usuarios directos");
        validatePositiveNumber(txtIndirectUserCount.getText(), "usuarios indirectos");
    }

    private void validatePositiveNumber(String value, String fieldName) throws ValidationException {
        InputValidator.validateNotEmpty(value, "El número de " + fieldName + " es obligatorio.");
        int number = Integer.parseInt(value);
        InputValidator.validatePositive(number, "El número de " + fieldName + " debe ser mayor a cero.");
    }
}
