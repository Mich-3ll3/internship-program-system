package mx.uv.internshipprogramsystem.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.ProfessorRegistrationManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.validations.InputCleaner;

public class RegisterProfessorFormController implements Initializable {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            RegisterProfessorFormController.class
        );

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

    @Override
    public void initialize(
            URL url,
            ResourceBundle resourceBundle
    ) {
        try {
            validatePermission(
                Permission.REGISTER_PROFESSOR
            );

            configureCoordinatorCheckBox();

            LOGGER.info(
                "Vista de registro de profesor cargada correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al registro de profesor.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );

            WindowManagerController.changeView(
                "AdminHomeDashboard.fxml"
            );
        }
    }

    @FXML
    private void goHome(
            ActionEvent event
    ) {
        WindowManagerController.changeView(
            "AdminHomeDashboard.fxml"
        );
    }

    @FXML
    private void goProfessorModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_PROFESSOR,
            "ProfessorModuleDashboard.fxml",
            "Acceso denegado al módulo de profesores."
        );
    }

    @FXML
    private void goInternModule(
            ActionEvent event
    ) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de estudiantes."
        );
    }

    @FXML
    private void logOut(
            ActionEvent event
    ) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    @FXML
    private void validateRegisterProfessorForm() {
        try {
            validatePermission(
                Permission.REGISTER_PROFESSOR
            );

            if (isFormValid()) {
                registerProfessor();
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al registro de profesor.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void configureCoordinatorCheckBox() {
        ProfessorDAO professorDAO =
            new ProfessorDAO();

        try {
            if (professorDAO.existsCoordinator()) {
                chkCoordinator.setSelected(
                    false
                );

                chkCoordinator.setDisable(
                    true
                );

                chkCoordinator.setText(
                    "Ya existe un coordinador registrado"
                );
            } else {
                chkCoordinator.setDisable(
                    false
                );

                chkCoordinator.setText(
                    "Asignar como coordinador"
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "No se pudo verificar si existe coordinador registrado.",
                businessException
            );

            chkCoordinator.setSelected(
                false
            );

            chkCoordinator.setDisable(
                true
            );

            chkCoordinator.setText(
                "No disponible"
            );

            FormAlertSupport.showError(
                "Error",
                "No se pudo verificar si existe un coordinador registrado."
            );
        }
    }

    private boolean isFormValid() {
        boolean isValid =
            true;

        String email =
            txtInstitutionalEmail.getText().trim();

        String name =
            txtName.getText().trim();

        String firstSurname =
            txtFirstSurname.getText().trim();

        String staffNumber =
            txtStaffNumber.getText().trim();

        if (email.isEmpty()
                || name.isEmpty()
                || firstSurname.isEmpty()
                || staffNumber.isEmpty()) {
            FormAlertSupport.showWarning(
                "Campos incompletos",
                "Por favor, llene todos los campos obligatorios."
            );

            isValid =
                false;
        } else if (!email.endsWith(
                "@uv.mx"
        )) {
            FormAlertSupport.showError(
                "Correo inválido",
                "Debe usar un correo institucional de docente (@uv.mx)."
            );

            isValid =
                false;
        } else if (!staffNumber.matches(
                "^\\d{6}$"
        )) {
            FormAlertSupport.showError(
                "Número de personal inválido",
                "Debe contener exactamente 6 dígitos numéricos."
            );

            isValid =
                false;
        }

        return isValid;
    }

    private void registerProfessor() {
        try {
            UserDTO user =
                buildUser();

            ProfessorDTO professor =
                buildProfessor(
                    0
                );

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

                FormAlertSupport.showInformation(
                    "Registro exitoso",
                    "El profesor ha sido registrado. "
                        + "Se envió un correo de activación."
                );

                clearForm();
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error de negocio al registrar profesor.",
                businessException
            );

            FormAlertSupport.showError(
                "Error de registro",
                businessException.getMessage()
            );
        }
    }

    private UserDTO buildUser() {
        String cleanEmail =
            InputCleaner.sanitizeText(
                txtInstitutionalEmail.getText()
            );

        String cleanName =
            InputCleaner.sanitizeText(
                txtName.getText()
            );

        String cleanFirstSurname =
            InputCleaner.sanitizeText(
                txtFirstSurname.getText()
            );

        String cleanSecondSurname =
            InputCleaner.sanitizeText(
                txtSecondSurname.getText()
            );

        UserDTO user =
            new UserDTO(
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

    private ProfessorDTO buildProfessor(
            int userId
    ) {
        String cleanStaffNumber =
            InputCleaner.sanitizeText(
                txtStaffNumber.getText()
            );

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

        chkCoordinator.setSelected(
            false
        );

        configureCoordinatorCheckBox();
    }

    private void openViewWithPermission(
            Permission permission,
            String fxmlName,
            String logMessage
    ) {
        try {
            validatePermission(
                permission
            );

            WindowManagerController.changeView(
                fxmlName
            );

            LOGGER.info(
                "Acceso permitido a la vista {}.",
                fxmlName
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                logMessage,
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void validatePermission(
            Permission permission
    ) throws BusinessException {
        AccessControlManager accessControlManager =
            new AccessControlManager();

        accessControlManager.validatePermission(
            UserSessionManager.getCurrentUser(),
            permission
        );
    }
}