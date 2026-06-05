package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class CoordinatorProfessorHomeDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            CoordinatorProfessorHomeDashboardController.class
        );

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goEducationalExperienceModule(ActionEvent event) {
        openViewWithPermission(
            Permission.REGISTER_EDUCATIONAL_EXPERIENCE,
            "EducationalExperienceRegisterDashboard.fxml",
            "Acceso denegado al módulo de experiencias educativas"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_INTERN,
            "InternModuleDashboard.fxml",
            "Acceso denegado al módulo de estudiantes"
        );
    }

    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_ORGANIZATION,
            "LinkedOrganizationManagementGUI.fxml",
            "Acceso denegado al módulo de organizaciones"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT,
            "ProjectsModuleDashboard.fxml",
            "Acceso denegado al módulo de proyectos"
        );
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.VALIDATE_INITIAL_FORMATS,
            "DocumentsModuleDashboard.fxml",
            "Acceso denegado al módulo de documentos"
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        openViewWithPermission(
            Permission.EVALUATE_REPORT,
            "ReportsModuleDashboard.fxml",
            "Acceso denegado al módulo de reportes"
        );
    }

    @FXML
    private void goResponsibleModule(ActionEvent event) {
        openViewWithPermission(
            Permission.CONSULT_PROJECT_RESPONSIBLE,
            "ProjectResponsibleModuleDashboard.fxml",
            "Acceso denegado al módulo de responsables"
        );
    }

    @FXML
    private void goProjectRequestModule(ActionEvent event) {
        openViewWithPermission(
            Permission.ASSIGN_PROJECT,
            "ProjectRequestModuleDashboard.fxml",
            "Acceso denegado al módulo de solicitudes"
        );
    }

    @FXML
    private void goTrackingModule(ActionEvent event) {
        openViewWithPermission(
            Permission.EVALUATE_REPORT,
            "TrackingModuleDashboard.fxml",
            "Acceso denegado al módulo de seguimiento"
        );
    }

    @FXML
    private void logOut(ActionEvent event) {
        UserSessionManager.clearSession();

        LOGGER.info(
            "Cierre de sesión realizado correctamente."
        );

        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    private void openViewWithPermission(
            Permission permission,
            String fxmlName,
            String logMessage
    ) {
        try {
            validatePermission(permission);

            LOGGER.info(
                "Acceso permitido a {}",
                fxmlName
            );

            WindowManagerController.changeView(
                fxmlName
            );
        } catch (BusinessException businessException) {
            handleAccessDenied(
                logMessage,
                businessException
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

    private void handleAccessDenied(
            String logMessage,
            BusinessException businessException
    ) {
        LOGGER.warn(
            logMessage,
            businessException
        );

        FormAlertSupport.showError(
            "Acceso denegado",
            "No tienes permisos para realizar esta acción."
        );
    }
}