package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.AccountActivationManager;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

public class ActivateAccountDashboardController
        implements ChangeListener<String> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ActivateAccountDashboardController.class
        );

    @FXML
    private TextField txtActivationToken;

    @FXML
    private PasswordField pwdNewPassword;

    @FXML
    private PasswordField pwdConfirmPassword;

    @FXML
    private Pane pneStrengthBarPart1;

    @FXML
    private Pane pneStrengthBarPart2;

    @FXML
    private Pane pneStrengthBarPart3;

    @FXML
    private Pane pneStrengthBarPart4;

    @FXML
    private Label lblMinLengthRequirement;

    @FXML
    private Label lblUppercaseRequirement;

    @FXML
    private Label lblNumberRequirement;

    @FXML
    private Label lblSymbolRequirement;

    @FXML
    private Label lblPasswordMatch;

    @FXML
    private void initialize() {
        pwdNewPassword.textProperty().addListener(this);
        pwdConfirmPassword.textProperty().addListener(this);

        updatePasswordStrength("");
        updatePasswordMatch();
    }

    @FXML
    private void handleActivateAccount() {
        String activationToken = txtActivationToken.getText();
        String newPassword = pwdNewPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();

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

            FormAlertSupport.showInformation(
                "Cuenta activada",
                "Tu cuenta fue activada correctamente. Ya puedes iniciar sesión."
            );

            WindowManagerController.changeView(
                "LoginDashboard.fxml"
            );
        } catch (DataAccessException exception) {
            LOGGER.warn("No se pudo procesar la activación de cuenta debido a un problema con los servicios del entorno.");
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("Servicio No Disponible");
            alert.setContentText("No se pudo establecer comunicación con el sistema central. Por favor, verifique su conexión a internet o intente más tarde.");
            alert.showAndWait();
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo activar la cuenta",
                businessException
            );

            FormAlertSupport.showError(
                "Error de activación",
                businessException.getMessage()
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado durante la activación de cuenta",
                exception
            );

            FormAlertSupport.showError(
                "Error de sistema",
                "No se pudo activar la cuenta."
            );
        }
    }

    @FXML
    private void handleOpenResendTokenView() {
        WindowManagerController.changeView(
            "ResendActivationTokenDashboard.fxml"
        );
    }

    @FXML
    private void handleBackToLogin() {
        WindowManagerController.changeView(
            "LoginDashboard.fxml"
        );
    }

    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldPassword,
            String newPassword
    ) {
        updatePasswordStrength(
            pwdNewPassword.getText()
        );

        updatePasswordMatch();
    }

    private void updatePasswordStrength(
            String password
    ) {
        boolean hasMinLength =
            password.length() >= 8;

        boolean hasUppercase =
            password.matches(".*[A-Z].*");

        boolean hasNumber =
            password.matches(".*[0-9].*");

        boolean hasSymbol =
            password.matches(".*[@#$%^&+=!].*");

        updateRequirementLabel(
            lblMinLengthRequirement,
            hasMinLength
        );

        updateRequirementLabel(
            lblUppercaseRequirement,
            hasUppercase
        );

        updateRequirementLabel(
            lblNumberRequirement,
            hasNumber
        );

        updateRequirementLabel(
            lblSymbolRequirement,
            hasSymbol
        );

        updateStrengthBars(
            hasMinLength,
            hasUppercase,
            hasNumber,
            hasSymbol
        );
    }

    private void updatePasswordMatch() {
        String password =
            pwdNewPassword.getText();

        String confirmPassword =
            pwdConfirmPassword.getText();

        if (confirmPassword.isEmpty()) {
            lblPasswordMatch.setVisible(false);
            lblPasswordMatch.setManaged(false);
        } else {
            lblPasswordMatch.setVisible(true);
            lblPasswordMatch.setManaged(true);

            updatePasswordMatchLabel(
                password,
                confirmPassword
            );
        }
    }

    private void updatePasswordMatchLabel(
            String password,
            String confirmPassword
    ) {
        if (password.equals(confirmPassword)) {
            lblPasswordMatch.setText(
                "✓ Las contraseñas coinciden"
            );

            lblPasswordMatch.setTextFill(
                Color.web("#16A34A")
            );
        } else {
            lblPasswordMatch.setText(
                "✗ Las contraseñas no coinciden"
            );

            lblPasswordMatch.setTextFill(
                Color.web("#EF4444")
            );
        }
    }

    private void updateRequirementLabel(
            Label requirementLabel,
            boolean isValid
    ) {
        if (isValid) {
            requirementLabel.setTextFill(
                Color.web("#16A34A")
            );
        } else {
            requirementLabel.setTextFill(
                Color.GRAY
            );
        }
    }

    private void updateStrengthBars(
            boolean hasMinLength,
            boolean hasUppercase,
            boolean hasNumber,
            boolean hasSymbol
    ) {
        int score = 0;

        if (hasMinLength) {
            score++;
        }

        if (hasUppercase) {
            score++;
        }

        if (hasNumber) {
            score++;
        }

        if (hasSymbol) {
            score++;
        }

        resetStrengthBars();
        updateStrengthBarColor(score);
    }

    private void updateStrengthBarColor(
            int score
    ) {
        if (score >= 1) {
            pneStrengthBarPart1.setStyle(
                "-fx-background-color: #EF4444; -fx-background-radius: 10;"
            );
        }

        if (score >= 2) {
            pneStrengthBarPart2.setStyle(
                "-fx-background-color: #F59E0B; -fx-background-radius: 10;"
            );
        }

        if (score >= 3) {
            pneStrengthBarPart3.setStyle(
                "-fx-background-color: #16A34A; -fx-background-radius: 10;"
            );
        }

        if (score >= 4) {
            pneStrengthBarPart4.setStyle(
                "-fx-background-color: #16A34A; -fx-background-radius: 10;"
            );
        }
    }

    private void resetStrengthBars() {
        String defaultStyle =
            "-fx-background-color: #E5E7EB; -fx-background-radius: 10;";

        pneStrengthBarPart1.setStyle(defaultStyle);
        pneStrengthBarPart2.setStyle(defaultStyle);
        pneStrengthBarPart3.setStyle(defaultStyle);
        pneStrengthBarPart4.setStyle(defaultStyle);
    }
}