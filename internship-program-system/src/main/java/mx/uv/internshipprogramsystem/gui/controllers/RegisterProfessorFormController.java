package mx.uv.internshipprogramsystem.gui.controllers;

import java.util.List;
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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import mx.uv.internshipprogramsystem.logic.PasswordGenerator;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

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
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView("AdminHomeDashboard.fxml");
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
    private void validateRegisterProfessorForm() {
        if (isFormValid()) {
            registerProfessor();
        }
    }

    private boolean isFormValid() {
        String email = txtInstitutionalEmail.getText().trim();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String staffNumber = txtStaffNumber.getText().trim();

        if (email.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || staffNumber.isEmpty()) {
            showNotification(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llene todos los campos obligatorios.");
            return false;
        }

        if (!email.endsWith("@uv.mx")) {
            showNotification(Alert.AlertType.ERROR, "Correo inválido", "Debe usar un correo institucional de docente (@uv.mx).");
            return false;
        }

        if (!staffNumber.matches("^\\d{6}$")) {
            showNotification(Alert.AlertType.ERROR, "Número de personal inválido", "Debe contener exactamente 6 dígitos numéricos.");
            return false;
        }

        return true;
    }

    private void registerProfessor() {
        try {
            String tempPassword = PasswordGenerator.generateSecurePassword();
             
            UserDTO user = new UserDTO(
                txtInstitutionalEmail.getText().trim(),
                tempPassword,
                txtName.getText().trim(),
                txtFirstSurname.getText().trim(),
                txtSecondSurname.getText().trim(),
                true,
                RolUsuario.PROFESOR
            );
            
            UserValidator userValidator = new UserValidator();
            userValidator.validateUserForCreation(user, tempPassword);
            
            UserDAO userDAO = new UserDAO();
            int userId = userDAO.create(user, tempPassword);
            
            ProfessorDTO professor = new ProfessorDTO(
                txtStaffNumber.getText().trim(),
                chkCoordinator.isSelected(),
                userId
            );
            
            ProfessorValidator validator = new ProfessorValidator();
            validator.validateStaffNumber(professor.getStaffNumber());

            ProfessorDAO professorDAO = new ProfessorDAO();
            if (professorDAO.create(professor)) {
                showNotification(Alert.AlertType.INFORMATION, "Registro exitoso", "El profesor ha sido registrado correctamente.");
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