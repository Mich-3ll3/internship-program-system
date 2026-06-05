package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UpdateProfessorDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            UpdateProfessorDashboardController.class
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

    private ProfessorDTO currentProfessor;

    private final ProfessorDAO professorDAO =
        new ProfessorDAO();

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

    public void setProfessorData(
            ProfessorDTO professor
    ) {
        currentProfessor =
            professor;

        txtName.setText(
            professor.getName()
        );

        txtFirstSurname.setText(
            professor.getFirstSurname()
        );

        txtSecondSurname.setText(
            professor.getSecondSurname()
        );

        txtInstitutionalEmail.setText(
            professor.getInstitutionalEmail()
        );

        txtStaffNumber.setText(
            String.valueOf(
                professor.getStaffNumber()
            )
        );

        chkCoordinator.setSelected(
            professor.getIsCoordinator()
        );

        txtInstitutionalEmail.setEditable(
            false
        );

        txtStaffNumber.setEditable(
            false
        );
    }

    @FXML
    private void handleUpdateAction() {
        try {
            validatePermission(
                Permission.UPDATE_PROFESSOR
            );

            if (isFormValid()) {
                updateProfessor();
            }
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado a la actualización de profesor.",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                businessException.getMessage()
            );
        }
    }

    private void updateProfessor() {
        try {
            validateCurrentProfessor();

            currentProfessor.setName(
                txtName.getText().trim()
            );

            currentProfessor.setFirstSurname(
                txtFirstSurname.getText().trim()
            );

            currentProfessor.setSecondSurname(
                txtSecondSurname.getText().trim()
            );

            currentProfessor.setIsCoordinator(
                chkCoordinator.isSelected()
            );

            currentProfessor.setRole(
                UserRole.PROFESSOR
            );

            UserValidator userValidator =
                new UserValidator();

            userValidator.validateUserForUpdate(
                currentProfessor
            );

            UserDAO userDAO =
                new UserDAO();

            boolean userUpdated =
                userDAO.update(
                    currentProfessor
                );

            boolean professorUpdated =
                professorDAO.update(
                    currentProfessor
                );

            if (userUpdated && professorUpdated) {
                FormAlertSupport.showInformation(
                    "Éxito",
                    "Los datos se actualizaron correctamente."
                );

                goProfessorModule(
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
                "Error al actualizar profesor.",
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

    private void validateCurrentProfessor()
            throws BusinessException {
        if (currentProfessor == null) {
            throw new BusinessException(
                "No se seleccionó un profesor para actualizar."
            );
        }
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