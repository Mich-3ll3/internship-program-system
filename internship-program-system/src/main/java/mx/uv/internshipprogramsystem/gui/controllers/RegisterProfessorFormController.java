package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.ProfessorRegistrationManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;

public class RegisterProfessorFormController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RegisterProfessorFormController.class);

    @FXML
    private TextField txtInstitutionalEmail;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtFirstSurname;
    @FXML
    private TextField txtSecondSurname;
    @FXML
    private TextField txtStaffNumber;
    @FXML
    private CheckBox chkCoordinator;

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
        boolean isValid = true;

        String email = txtInstitutionalEmail.getText().trim();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String staffNumber = txtStaffNumber.getText().trim();

        if (email.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || staffNumber.isEmpty()) {
            showNotification(
                Alert.AlertType.WARNING,
                "Campos incompletos",
                "Por favor, llene todos los campos obligatorios."
            );
            isValid = false;
        } else if (!email.endsWith("@uv.mx")) {
            showNotification(
                Alert.AlertType.ERROR,
                "Correo inválido",
                "Debe usar un correo institucional de docente (@uv.mx)."
            );
            isValid = false;
        } else if (!staffNumber.matches("^\\d{6}$")) {
            showNotification(
                Alert.AlertType.ERROR,
                "Número de personal inválido",
                "Debe contener exactamente 6 dígitos numéricos."
            );
            isValid = false;
        }
        return isValid;
    }

    private void registerProfessor() {
        try {
            UserDTO user = buildUser();
            ProfessorDTO professor = buildProfessor(0);
            ProfessorRegistrationManager professorRegistrationManager =
                new ProfessorRegistrationManager();

            boolean wasCreated =
                professorRegistrationManager.registerProfessor(
                    user,
                    professor
                );

            if (wasCreated) {
                LOGGER.info(
                    "Profesor registrado correctamente."
                );

                showNotification(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "El profesor ha sido registrado. "
                    + "Se envió un correo de activación."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error de negocio al registrar profesor",
                businessException
            );
            showNotification(
                Alert.AlertType.ERROR,
                "Error de registro",
                businessException.getMessage()
            );
        }
    }

    private UserDTO buildUser() {
        String cleanEmail = InputCleaner.sanitizeText(txtInstitutionalEmail.getText());
        String cleanName = InputCleaner.sanitizeText(txtName.getText());
        String cleanFirstSurname = InputCleaner.sanitizeText(txtFirstSurname.getText());
        String cleanSecondSurname = InputCleaner.sanitizeText(txtSecondSurname.getText());

        UserDTO user = new UserDTO(
            cleanEmail,
            null,
            cleanName,
            cleanFirstSurname,
            cleanSecondSurname,
            false,
            UserRole.PROFESSOR
        );
        return user;
    }

    private ProfessorDTO buildProfessor(int userId) {
        String cleanStaffNumber = InputCleaner.sanitizeText(txtStaffNumber.getText());
        ProfessorDTO professor = 
        new ProfessorDTO(
            cleanStaffNumber, 
            chkCoordinator.isSelected(),
            userId
        );
        return professor;
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

    private void showNotification(
            Alert.AlertType type,
            String title,
            String content
    ) {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}