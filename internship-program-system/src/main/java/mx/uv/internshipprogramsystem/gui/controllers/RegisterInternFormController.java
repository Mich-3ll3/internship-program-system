package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
=======
>>>>>>> feature/setup-gui
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
<<<<<<< HEAD
import mx.uv.internshipprogramsystem.logic.security.UserRegistrationManager;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
=======
import mx.uv.internshipprogramsystem.logic.managers.InternRegistrationManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
>>>>>>> feature/setup-gui

public class RegisterInternFormController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RegisterInternFormController.class);

    @FXML
    private TextField txtInstitutionalEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtFirstSurname;

    @FXML
    private TextField txtSecondSurname;

    @FXML
    private TextField txtEnrollment;

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
        boolean isValid = true;
        String email = txtInstitutionalEmail.getText().trim();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String enrollment = txtEnrollment.getText().trim();

        if (email.isEmpty() || name.isEmpty()
                || firstSurname.isEmpty() || enrollment.isEmpty()) {
            showNotification(
                Alert.AlertType.WARNING,
                "Campos incompletos",
                "Por favor, llene todos los campos obligatorios."
            );
            isValid = false;
        } else if (!email.endsWith("@estudiantes.uv.mx")) {
            showNotification(
                Alert.AlertType.ERROR,
                "Correo inválido",
                "Debe usar un correo institucional (@estudiantes.uv.mx)."
            );
            isValid = false;
        } else if (!enrollment.matches("^zS\\d{8}$")) {
            showNotification(
                Alert.AlertType.ERROR,
                "Matrícula inválida",
                "El formato debe ser zS seguido de 8 números."
            );
            isValid = false;
        }

        return isValid;
    }

    private void registerIntern() {
        try {
            UserDTO user = buildUser();
<<<<<<< HEAD

            UserValidator userValidator = new UserValidator();
            userValidator.validateUserForCreation(user);

            UserRegistrationManager registrationManager =
                new UserRegistrationManager();

            int userId = registrationManager.registerUser(user);

            InternDTO intern = new InternDTO(
                txtEnrollment.getText().trim(),
                userId
            );

            InternValidator internValidator = new InternValidator();
            internValidator.validateEnrollmentNumber(
                intern.getEnrollmentNumber()
            );

            InternDAO internDAO = new InternDAO();
            boolean wasCreated = internDAO.create(intern);
=======
            InternDTO intern = buildIntern(0);
            InternRegistrationManager internRegistrationManager =
                new InternRegistrationManager();
            boolean wasCreated = internRegistrationManager.registerIntern(user, intern);
>>>>>>> feature/setup-gui

            if (wasCreated) {
                LOGGER.info(
                    "Estudiante registrado correctamente con correo {}",
                    user.getInstitutionalEmail()
                );

                showNotification(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "El estudiante ha sido registrado. "
                    + "Se envió un correo de activación."
                );

                clearForm();
            }
        } catch (BusinessException exception) {
<<<<<<< HEAD
            LOGGER.error("Error de negocio al registrar estudiante", exception);
=======
            LOGGER.error(
                "Error de negocio al registrar estudiante",
                exception
            );

>>>>>>> feature/setup-gui
            showNotification(
                Alert.AlertType.ERROR,
                "Error de registro",
                exception.getMessage()
            );
<<<<<<< HEAD
        } catch (Exception exception) {
            LOGGER.error("Error inesperado al registrar estudiante", exception);
            showNotification(
                Alert.AlertType.ERROR,
                "Error de sistema",
                "No se pudo completar el registro."
            );
=======
>>>>>>> feature/setup-gui
        }
    }

    private UserDTO buildUser() {
<<<<<<< HEAD
        UserDTO user = new UserDTO(
            txtInstitutionalEmail.getText().trim(),
            null,
            txtName.getText().trim(),
            txtFirstSurname.getText().trim(),
            txtSecondSurname.getText().trim(),
            false,
            UserRole.STUDENT
        );

        return user;
    }

=======
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
            UserRole.STUDENT
        );
        return user;
    }

    private InternDTO buildIntern(int userId) {
        String cleanEnrollment = InputCleaner.sanitizeText(txtEnrollment.getText());
        InternDTO intern = new InternDTO(cleanEnrollment, userId);
        return intern;
    }

>>>>>>> feature/setup-gui
    @FXML
    private void clearForm() {
        txtInstitutionalEmail.clear();
        txtName.clear();
        txtFirstSurname.clear();
        txtSecondSurname.clear();
        txtEnrollment.clear();
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