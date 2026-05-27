package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
<<<<<<< HEAD
import mx.uv.internshipprogramsystem.logic.security.AccountActivationManager;
=======
import mx.uv.internshipprogramsystem.logic.managers.AccountActivationManager;
import mx.uv.internshipprogramsystem.logic.managers.ActivationTokenResendManager;
>>>>>>> feature/setup-gui

public class ActivateAccountDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ActivateAccountDashboardController.class
        );

    @FXML
    private TextField txtActivationToken;
<<<<<<< HEAD

    @FXML
    private PasswordField pwdNewPassword;

    @FXML
    private PasswordField pwdConfirmPassword;
=======
    @FXML
    private PasswordField pwdNewPassword;
    @FXML
    private PasswordField pwdConfirmPassword;
    @FXML
    private TextField txtInstitutionalEmail;

>>>>>>> feature/setup-gui

    @FXML
    private void handleActivateAccount() {
        String activationToken =
            txtActivationToken.getText();

        String newPassword =
            pwdNewPassword.getText();

        String confirmPassword =
            pwdConfirmPassword.getText();

        try {
            AccountActivationManager accountActivationManager =
                new AccountActivationManager();

            accountActivationManager.activateAccount(
                activationToken,
                newPassword,
                confirmPassword
            );

            LOGGER.info(
                "Cuenta activada correctamente."
            );

            showNotification(
                Alert.AlertType.INFORMATION,
                "Cuenta activada",
                "Tu cuenta fue activada correctamente. "
                    + "Ya puedes iniciar sesión."
            );

            WindowManagerController.changeView(
                "LoginDashboard.fxml"
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo activar la cuenta",
                businessException
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de activación",
                businessException.getMessage()
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado durante "
                    + "la activación de cuenta",
                exception
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de sistema",
                "No se pudo activar la cuenta."
            );
        }
    }

    @FXML
<<<<<<< HEAD
=======
    private void handleResendActivationToken() {
        String institutionalEmail =
            txtInstitutionalEmail.getText();

        try {
            ActivationTokenResendManager activationTokenResendManager =
                new ActivationTokenResendManager();

            activationTokenResendManager.resendActivationToken(
                institutionalEmail
            );

            LOGGER.info(
                "Solicitud de reenvío de token procesada."
            );

            showNotification(
                Alert.AlertType.INFORMATION,
                "Token enviado",
                "Si la cuenta existe y no está activa, "
                + "se enviará un nuevo token de activación."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo reenviar el token de activación",
                businessException
            );

            showNotification(
                Alert.AlertType.ERROR,
                "Error de reenvío",
                businessException.getMessage()
            );
        }
    }

    @FXML
>>>>>>> feature/setup-gui
    private void handleBackToLogin() {
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
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