package mx.uv.internshipprogramsystem.gui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class AdminHomeDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            AdminHomeDashboardController.class
        );

    @FXML
    private Label lblDate;

    @FXML
    private Label lblWelcomeUser;

    @FXML
    private void initialize() {
        try {
            validateAdministratorAccess();
            initializeDate();
            initializeUserInformation();

            LOGGER.info(
                "Dashboard de administrador cargado correctamente."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al dashboard de administrador",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                "No tienes permisos para acceder a esta vista."
            );

            WindowManagerController.changeView(
                "LoginDashboard.fxml"
            );
        }
    }

    private void validateAdministratorAccess()
            throws BusinessException {
        validatePermission(
            Permission.CONSULT_PROFESSOR
        );
    }

    private void initializeDate() {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy",
                new Locale("es", "MX")
            );

        String formattedDate =
            LocalDate.now().format(
                formatter
            );

        lblDate.setText(
            formattedDate
        );
    }

    private void initializeUserInformation() {
        String userName =
            UserSessionManager
                .getCurrentUser()
                .getName();

        lblWelcomeUser.setText(
            "Bienvenido, " + userName
        );
    }

    @FXML
    private void goProfile(ActionEvent event) {
        WindowManagerController.changeView(
            "UserProfileDashboard.fxml"
        );
    }

    @FXML
    private void goHome(ActionEvent event) {
        WindowManagerController.changeView(
            "AdminHomeDashboard.fxml"
        );
    }

    @FXML
    private void goProfessorModule(ActionEvent event) {
        try {
            validatePermission(
                Permission.CONSULT_PROFESSOR
            );

            WindowManagerController.changeView(
                "ProfessorModuleDashboard.fxml"
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Acceso denegado al módulo de profesores",
                businessException
            );

            FormAlertSupport.showError(
                "Acceso denegado",
                "No tienes permisos para acceder al módulo de profesores."
            );
        }
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