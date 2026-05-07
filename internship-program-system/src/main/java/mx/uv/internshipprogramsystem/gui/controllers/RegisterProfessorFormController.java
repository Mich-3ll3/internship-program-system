package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterProfessorFormController {

    private static final Logger LOGGER = Logger.getLogger(RegisterProfessorFormController.class.getName());

    @FXML private TextField txtInstitutionalEmail;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtStaffNumber;
    @FXML private CheckBox chkCoordinator;

    @FXML
    private void validateRegisterProfessorForm() {
        if (isFormValid()) {
            registerProfessor();
        }
    }

    private boolean isFormValid() {
        String email = txtInstitutionalEmail.getText().trim();
        String password = txtPassword.getText();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String staffNumber = txtStaffNumber.getText().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || staffNumber.isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llene todos los campos obligatorios.");
            return false;
        }

        if (!email.endsWith("@uv.mx")) {
            showNotification(Alert.AlertType.ERROR, "Correo inválido", "Debe usar un correo institucional de docente (@uv.mx).");
            return false;
        }

        if (password.length() < 8) {
            showNotification(Alert.AlertType.ERROR, "Contraseña débil", "La contraseña debe tener al menos 8 caracteres.");
            return false;
        }

        if (!staffNumber.matches("^\\d{6}$")) {
            showNotification(Alert.AlertType.ERROR, "Número de personal inválido", "Debe contener exactamente 6 dígitos numéricos.");
            return false;
        }

        return true;
    }

    private void registerProfessor() {
        ProfessorDTO newProfessor = new ProfessorDTO();
        newProfessor.setInstitucionalEmail(txtInstitutionalEmail.getText().trim());
        newProfessor.setPassword(txtPassword.getText());
        newProfessor.setName(txtName.getText().trim());
        newProfessor.setFirstSurname(txtFirstSurname.getText().trim());
        newProfessor.setSecondSurname(txtSecondSurname.getText().trim());
        newProfessor.setStaffNumber(Integer.parseInt(txtStaffNumber.getText().trim()));
        newProfessor.setIsCoordinator(chkCoordinator.isSelected());
        newProfessor.setIsActive(true);

        try {
            ProfessorDAO professorDAO = new ProfessorDAO();
            if (professorDAO.create(newProfessor)) {
                showNotification(Alert.AlertType.INFORMATION, "Registro exitoso", "El profesor ha sido registrado correctamente.");
                clearForm();
            }
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar profesor", e);
            showNotification(Alert.AlertType.ERROR, "Error de registro", e.getMessage());
        } catch (NumberFormatException e) {
            showNotification(Alert.AlertType.ERROR, "Error de formato", "El número de personal debe ser un valor numérico.");
        }
    }

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
        txtPassword.clear();
        txtName.clear();
        txtFirstSurname.clear();
        txtSecondSurname.clear();
        txtStaffNumber.clear();
        chkCoordinator.setSelected(false);
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}