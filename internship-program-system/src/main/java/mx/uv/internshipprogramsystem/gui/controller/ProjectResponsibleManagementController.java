package mx.uv.internshipprogramsystem.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectResponsibleManagementController {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastNameFather;

    @FXML
    private TextField txtLastNameMother;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPosition;

    @FXML
    private ComboBox<String> cmbOrganization;

    @FXML
    private TextField txtSearchEmail;

    @FXML
    private void validateResponsibleForm() {
        try {
            validateRequiredFields();
            validateEmail(txtEmail.getText());
            FormAlertSupport.showInformation("Validación exitosa", "Los datos del responsable cumplen con el formato requerido.");
        } catch (ValidationException exception) {
            FormAlertSupport.showWarning("Error de validación", exception.getMessage());
        }
    }

    @FXML
    private void validateSearchResponsible() {
        try {
            InputValidator.validateNotEmpty(txtSearchEmail.getText(), "Ingrese el correo del responsable para buscar.");
            validateEmail(txtSearchEmail.getText());
            FormAlertSupport.showInformation("Búsqueda válida", "El correo del responsable puede consultarse.");
        } catch (ValidationException exception) {
            FormAlertSupport.showWarning("Error de validación", exception.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        txtFirstName.clear();
        txtLastNameFather.clear();
        txtLastNameMother.clear();
        txtEmail.clear();
        txtPosition.clear();
        cmbOrganization.getSelectionModel().clearSelection();
    }

    private void validateRequiredFields() throws ValidationException {
        InputValidator.validateNotEmpty(txtFirstName.getText(), "El nombre es obligatorio.");
        InputValidator.validateNotEmpty(txtLastNameFather.getText(), "El apellido paterno es obligatorio.");
        InputValidator.validateNotEmpty(txtEmail.getText(), "El correo es obligatorio.");
        InputValidator.validateNotEmpty(txtPosition.getText(), "El cargo es obligatorio.");
        InputValidator.validateNotNull(cmbOrganization.getValue(), "Debe seleccionar una organización.");
    }

    private void validateEmail(String email) throws ValidationException {
        if (!email.matches(EMAIL_PATTERN)) {
            throw new ValidationException("El correo del responsable no tiene un formato válido.");
        }
    }
}
