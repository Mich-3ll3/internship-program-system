package mx.uv.internshipprogramsystem.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterInternFormController {

    private static final Logger LOGGER = Logger.getLogger(RegisterInternFormController.class.getName());

    @FXML private TextField txtInstitutionalEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtEnrollment;

    @FXML
    private void validateRegisterInternForm() {
        if (isFormValid()) {
            registerIntern();
        }
    }

    private boolean isFormValid() {
        String email = txtInstitutionalEmail.getText().trim();
        String password = txtPassword.getText();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String enrollment = txtEnrollment.getText().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || enrollment.isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llene todos los campos obligatorios.");
            return false;
        }

        if (!email.endsWith("@estudiantes.uv.mx")) {
            showNotification(Alert.AlertType.ERROR, "Correo inválido", "Debe usar un correo institucional (@estudiantes.uv.mx).");
            return false;
        }

        if (password.length() < 8) {
            showNotification(Alert.AlertType.ERROR, "Contraseña débil", "La contraseña debe tener al menos 8 caracteres.");
            return false;
        }

        if (!enrollment.matches("^zS\\d{8}$")) {
            showNotification(Alert.AlertType.ERROR, "Matrícula inválida", "El formato debe ser zS seguido de 8 números.");
            return false;
        }

        return true;
    }

    private void registerIntern() {
        InternDTO newIntern = new InternDTO();
        newIntern.setInstitucionalEmail(txtInstitutionalEmail.getText().trim());
        newIntern.setPassword(txtPassword.getText());
        newIntern.setName(txtName.getText().trim());
        newIntern.setFirstSurname(txtFirstSurname.getText().trim());
        newIntern.setSecondSurname(txtSecondSurname.getText().trim());
        newIntern.setEnrollmentNumber(txtEnrollment.getText().trim());
        newIntern.setIsActive(true);

        try {
            InternDAO internDAO = new InternDAO();
            if (internDAO.create(newIntern)) {
                showNotification(Alert.AlertType.INFORMATION, "Registro exitoso", "El estudiante ha sido registrado correctamente.");
                clearForm();
            }
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar estudiante", e);
            showNotification(Alert.AlertType.ERROR, "Error de registro", e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
        txtPassword.clear();
        txtName.clear();
        txtFirstSurname.clear();
        txtSecondSurname.clear();
        txtEnrollment.clear();
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
