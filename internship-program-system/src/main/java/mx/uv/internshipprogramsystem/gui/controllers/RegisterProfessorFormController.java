package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
=======
>>>>>>> feature/setup-gui
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
<<<<<<< HEAD
import mx.uv.internshipprogramsystem.logic.security.UserRegistrationManager;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;
=======
import mx.uv.internshipprogramsystem.logic.managers.ProfessorRegistrationManager;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;
>>>>>>> feature/setup-gui

public class RegisterProfessorFormController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(RegisterProfessorFormController.class);

    @FXML
    private TextField txtInstitutionalEmail;
<<<<<<< HEAD

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtFirstSurname;

    @FXML
    private TextField txtSecondSurname;

    @FXML
    private TextField txtStaffNumber;

=======
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtFirstSurname;
    @FXML
    private TextField txtSecondSurname;
    @FXML
    private TextField txtStaffNumber;
>>>>>>> feature/setup-gui
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
<<<<<<< HEAD
=======

>>>>>>> feature/setup-gui
        String email = txtInstitutionalEmail.getText().trim();
        String name = txtName.getText().trim();
        String firstSurname = txtFirstSurname.getText().trim();
        String staffNumber = txtStaffNumber.getText().trim();

<<<<<<< HEAD
        if (email.isEmpty() || name.isEmpty()
                || firstSurname.isEmpty() || staffNumber.isEmpty()) {
=======
        if (email.isEmpty() || name.isEmpty() || firstSurname.isEmpty() || staffNumber.isEmpty()) {
>>>>>>> feature/setup-gui
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
<<<<<<< HEAD

=======
>>>>>>> feature/setup-gui
        return isValid;
    }

    private void registerProfessor() {
        try {
            UserDTO user = buildUser();
<<<<<<< HEAD
            validateUser(user);

            UserRegistrationManager registrationManager =
                new UserRegistrationManager();

            int userId = registrationManager.registerUser(user);
            ProfessorDTO professor = buildProfessor(userId);

            validateProfessor(professor);

            ProfessorDAO professorDAO = new ProfessorDAO();
            boolean wasCreated = professorDAO.create(professor);

            if (wasCreated) {
                LOGGER.info(
                    "Profesor registrado correctamente: {}",
                    user.getInstitutionalEmail()
=======
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
>>>>>>> feature/setup-gui
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
<<<<<<< HEAD

=======
>>>>>>> feature/setup-gui
            showNotification(
                Alert.AlertType.ERROR,
                "Error de registro",
                businessException.getMessage()
            );
<<<<<<< HEAD
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado al registrar profesor",
                exception
            );

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
            UserRole.PROFESSOR
        );

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
            UserRole.PROFESSOR
        );
>>>>>>> feature/setup-gui
        return user;
    }

    private ProfessorDTO buildProfessor(int userId) {
<<<<<<< HEAD
        ProfessorDTO professor = new ProfessorDTO(
            txtStaffNumber.getText().trim(),
            chkCoordinator.isSelected(),
            userId
        );

        return professor;
    }

    private void validateUser(UserDTO user) throws BusinessException {
        UserValidator userValidator = new UserValidator();
        userValidator.validateUserForCreation(user);
    }

    private void validateProfessor(ProfessorDTO professor)
            throws BusinessException {
        ProfessorValidator professorValidator = new ProfessorValidator();
        professorValidator.validateStaffNumber(professor.getStaffNumber());
    }

=======
        String cleanStaffNumber = InputCleaner.sanitizeText(txtStaffNumber.getText());
        ProfessorDTO professor = 
        new ProfessorDTO(
            cleanStaffNumber, 
            chkCoordinator.isSelected(),
            userId
        );
        return professor;
    }

>>>>>>> feature/setup-gui
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