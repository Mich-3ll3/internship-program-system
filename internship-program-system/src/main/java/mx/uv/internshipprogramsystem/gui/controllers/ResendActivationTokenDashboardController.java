package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.ActivationTokenResendManager;

public class ResendActivationTokenDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ResendActivationTokenDashboardController.class
        );

    @FXML
    private TextField txtInstitutionalEmail;

    @FXML
    private void handleResendToken() {
        String institutionalEmail =
            txtInstitutionalEmail.getText();

        try {
            ActivationTokenResendManager activationTokenResendManager =
                new ActivationTokenResendManager();

            activationTokenResendManager.resendActivationToken(
                institutionalEmail
            );

            LOGGER.info(
                "Token de activación reenviado correctamente a {}",
                institutionalEmail
            );

            displayAlert(
                Alert.AlertType.INFORMATION,
                "Token enviado",
                "Se envió un nuevo token de activación a tu correo."
            );

            WindowManagerController.changeView(
                "ActivateAccountDashboard.fxml"
            );
        } catch (DataAccessException exception) {
            LOGGER.warn("No se pudo procesar la solicitud debido a un problema con los servicios del entorno.");
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("Servicio No Disponible");
            alert.setContentText("No se pudo establecer comunicación con el sistema central. Por favor, verifique su conexión a internet o intente más tarde.");
            alert.showAndWait();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo reenviar el token de activación",
                businessException
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error",
                businessException.getMessage()
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado al reenviar token de activación",
                exception
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error del sistema",
                "No se pudo reenviar el token de activación."
            );
        }
    }

    @FXML
    private void handleBackToActivation() {
        WindowManagerController.changeView(
            "ActivateAccountDashboard.fxml"
        );
    }

    private void displayAlert(
            Alert.AlertType alertType,
            String title,
            String content
    ) {
        Alert alert = new Alert(alertType);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}