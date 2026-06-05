package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UpdateInternDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            UpdateInternDashboardController.class
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
    private TextField txtEnrollment;

    private InternDTO currentIntern;

    private final InternDAO internDAO =
        new InternDAO();

    @FXML
    private void goHome(
            ActionEvent event
    ) {
        WindowManagerController.goBack();
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

    public void setInternData(
            InternDTO intern
    ) {
        currentIntern =
            intern;

        txtName.setText(
            intern.getName()
        );

        txtFirstSurname.setText(
            intern.getFirstSurname()
        );

        txtSecondSurname.setText(
            intern.getSecondSurname()
        );

        txtInstitutionalEmail.setText(
            intern.getInstitutionalEmail()
        );

        txtEnrollment.setText(
            intern.getEnrollmentNumber()
        );

        txtInstitutionalEmail.setEditable(
            false
        );

        txtEnrollment.setEditable(
            false
        );
    }

    @FXML
    private void handleUpdateAction() {
        try {
            validatePermission(
                Permission.UPDATE_INTERN
            );

            if (isFormValid()) {
                updateIntern();
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado a la actualización de estudiante.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void updateIntern() {
        try {
            validateCurrentIntern();

            currentIntern.setName(
                txtName.getText().trim()
            );

            currentIntern.setFirstSurname(
                txtFirstSurname.getText().trim()
            );

            currentIntern.setSecondSurname(
                txtSecondSurname.getText().trim()
            );

            currentIntern.setRole(
                UserRole.STUDENT
            );

            UserValidator userValidator =
                new UserValidator();

            userValidator.validateUserForUpdate(
                currentIntern
            );

            UserDAO userDAO =
                new UserDAO();

            boolean userUpdated =
                userDAO.update(
                    currentIntern
                );

            boolean internUpdated =
                internDAO.update(
                    currentIntern
                );

            if (userUpdated && internUpdated) {
                FormAlertSupport.showInformation(
                    "Éxito",
                    "Los datos se actualizaron correctamente."
                );

                goInternModule(
                    null
                );
            } else {
                FormAlertSupport.showWarning(
                    "Atención",
                    "No se pudieron actualizar todos los registros."
                );
            }
        } catch (BusinessException businessException) {
            LOGGER.error(
                "Error al actualizar estudiante.",
                businessException
            );

            FormAlertSupport.showError(
                "Error",
                businessException.getMessage()
            );
        }
    }

    private boolean isFormValid() {
        boolean isValid =
            true;

        if (txtName.getText().trim().isEmpty()
                || txtFirstSurname.getText().trim().isEmpty()) {
            FormAlertSupport.showWarning(
                "Campos vacíos",
                "El nombre y primer apellido son obligatorios."
            );

            isValid =
                false;
        }

        return isValid;
    }

    private void validateCurrentIntern()
            throws BusinessException {
        if (currentIntern == null) {
            throw new BusinessException(
                "No se seleccionó un estudiante para actualizar."
            );
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