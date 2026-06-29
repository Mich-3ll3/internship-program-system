package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class CoordinatorProfessorHomeDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            CoordinatorProfessorHomeDashboardController.class
        );

    @FXML
    private Label lblCoordinatorName;

    @FXML
    private void initialize() {
        initializeProfile();
    }

    private void initializeProfile() {
        UserDTO currentUser = UserSessionManager.getCurrentUser();
        if (currentUser != null) {
            lblCoordinatorName.setText(currentUser.getFullName());
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "CoordinatorProfessorHomeDashboard.fxml"
        );
    }

    @FXML
    private void goEducationalExperienceModule(ActionEvent event) {
        WindowManagerController.changeView(
            "EducationalExperienceRegisterDashboard.fxml"
        );
    }

    @FXML
    private void goInternModule(ActionEvent event) {
        WindowManagerController.changeView(
            "InternModuleDashboard.fxml"
        );
    }

    @FXML
    private void goLinkedOrganizationModule(ActionEvent event) {
        WindowManagerController.changeView(
            "LinkedOrganizationManagementGUI.fxml"
        );
    }

    @FXML
    private void goProjectsModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de proyectos."
        );

        WindowManagerController.changeView(
            "ProjectsModuleDashboard.fxml"
        );
    }

    @FXML
    private void goDocumentsModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProfessorDocumentDashboard.fxml"
        );
    }

    @FXML
    private void goReportsModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProfessorReportDashboard.fxml"
        );
    }

    @FXML
    private void goResponsibleModule(ActionEvent event) {
        WindowManagerController.changeView(
            "ProjectResponsibleModuleDashboard.fxml"
        );
    }

    @FXML
    private void goProjectRequestModule(ActionEvent event) {
        LOGGER.info("Acceso al módulo de solicitudes.");
        WindowManagerController.changeView("ProjectRequestDashboard.fxml");
    }

    @FXML
    private void goActivityModule(ActionEvent event) {
        LOGGER.info(
            "Acceso al módulo de actividades."
        );
        WindowManagerController.changeView(
            "RegisterActivityDashboard.fxml"
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
}