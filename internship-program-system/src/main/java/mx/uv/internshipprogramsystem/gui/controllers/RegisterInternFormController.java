package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.PasswordGenerator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class RegisterInternFormController {

    private static final Logger LOGGER = Logger.getLogger(RegisterInternFormController.class.getName());

    @FXML private TextField txtInstitutionalEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtFirstSurname;
    @FXML private TextField txtSecondSurname;
    @FXML private TextField txtEnrollment;
    
    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.goBack();
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        WindowManagerController.changeView("ProfessorModuleDashboard.fxml");
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView("InternModuleDashboard.fxml");
    }

    @FXML
    private void logOut(ActionEvent event) {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void validateRegisterInternForm() {
        if (isFormValid()) {
            registerIntern();
        }
    }

    private boolean isFormValid() {
        String email = txtInstitutionalEmail.getText().trim();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String enrollment = txtEnrollment.getText().trim();

        if (email.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || enrollment.isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llene todos los campos obligatorios.");
            return false;
        }

        if (!email.endsWith("@estudiantes.uv.mx")) {
            showNotification(Alert.AlertType.ERROR, "Correo inválido", "Debe usar un correo institucional (@estudiantes.uv.mx).");
            return false;
        }

        if (!enrollment.matches("^zS\\d{8}$")) {
            showNotification(Alert.AlertType.ERROR, "Matrícula inválida", "El formato debe ser zS seguido de 8 números.");
            return false;
        }

        return true;
    }

    private void registerIntern() {
        try {
            String tempPassword = PasswordGenerator.generateSecurePassword();
             
            UserDTO user = new UserDTO(
                txtInstitutionalEmail.getText().trim(),
                tempPassword,
                txtName.getText().trim(),
                txtFirstSurname.getText().trim(),
                txtSecondSurname.getText().trim(),
                true,
                RolUsuario.ESTUDIANTE
            );
            
            UserValidator userValidator = new UserValidator();
            userValidator.validateUserForCreation(user, tempPassword);
            
            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, tempPassword);
            
            InternDTO intern = new InternDTO(
                txtEnrollment.getText().trim(),
                userId
            );
            
            InternValidator validator = new InternValidator();
            validator.validateEnrollmentNumber(intern.getEnrollmentNumber());
            
            InternDAO internDAO = new InternDAO();
            if (internDAO.create(intern)) {
                showNotification(Alert.AlertType.INFORMATION, "Registro exitoso", "El estudiante ha sido registrado correctamente.");
                clearForm();
            }
        } catch (BusinessException exception) {
            LOGGER.log(Level.SEVERE, "Error de negocio: " + exception.getMessage(), exception);
            showNotification(Alert.AlertType.ERROR, "Error de registro",
                exception.getMessage() + " | " + exception.getCause().getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error inesperado en el sistema", ex);
            showNotification(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo completar el registro.");
        }
    }

    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
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
