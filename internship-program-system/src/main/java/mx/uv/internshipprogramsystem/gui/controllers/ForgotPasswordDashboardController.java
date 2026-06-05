package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.PasswordRecoveryManager;
import mx.uv.internshipprogramsystem.logic.managers.PasswordRecoveryRequestManager;

public class ForgotPasswordDashboardController implements ChangeListener<String> {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ForgotPasswordDashboardController.class);

    @FXML
    private TextField txtRecoveryToken;
    @FXML
    private PasswordField pwdNewPassword;
    @FXML
    private PasswordField pwdConfirmPassword;
    @FXML
    private TextField txtInstitutionalEmail;
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
    private void handleResetPassword() {
        String recoveryToken = txtRecoveryToken.getText();
        String newPassword = pwdNewPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();

        try {
            PasswordRecoveryManager passwordRecoveryManager =
                new PasswordRecoveryManager();

            passwordRecoveryManager.resetPassword(
                recoveryToken,
                newPassword,
                confirmPassword
            );

            LOGGER.info("Contraseña actualizada correctamente.");

            displayAlert(
                Alert.AlertType.INFORMATION,
                "Operación exitosa",
                "La contraseña fue actualizada correctamente."
            );

            WindowManagerController.changeView("LoginDashboard.fxml");
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Error al recuperar contraseña",
                businessException
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error",
                businessException.getMessage()
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado al recuperar contraseña",
                exception
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error del sistema",
                "No se pudo recuperar la contraseña."
            );
        }
    }
    
    @FXML
    private void handleSendRecoveryToken() {
        String institutionalEmail = txtInstitutionalEmail.getText();

        try {
            PasswordRecoveryRequestManager passwordRecoveryRequestManager =
                new PasswordRecoveryRequestManager();

            passwordRecoveryRequestManager.sendRecoveryToken(
                institutionalEmail
            );

            displayAlert(
                Alert.AlertType.INFORMATION,
                "Token enviado",
                "Se envió un token de recuperación a tu correo."
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "No se pudo enviar token de recuperación",
                businessException
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error",
                businessException.getMessage()
            );
        }
    }

    @FXML
    private void handleBackToLogin() {
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
    
    @Override
    public void changed(
            ObservableValue<? extends String> observable,
            String oldPassword,
            String newPassword
    ) {
        updatePasswordStrength(pwdNewPassword.getText());
        updatePasswordMatch();
    }

    private void updatePasswordStrength(String password) {
        boolean hasMinLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[@#$%^&+=!].*");

        updateRequirementLabel(lblMinLengthRequirement, hasMinLength);
        updateRequirementLabel(lblUppercaseRequirement, hasUppercase);
        updateRequirementLabel(lblNumberRequirement, hasNumber);
        updateRequirementLabel(lblSymbolRequirement, hasSymbol);

        updateStrengthBars(
            hasMinLength,
            hasUppercase,
            hasNumber,
            hasSymbol
        );
    }
    
    private void updatePasswordMatch() {
        String password = pwdNewPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();

        if (confirmPassword.isEmpty()) {
            lblPasswordMatch.setVisible(false);
            lblPasswordMatch.setManaged(false);
        } else {
            lblPasswordMatch.setVisible(true);
            lblPasswordMatch.setManaged(true);

            if (password.equals(confirmPassword)) {
                lblPasswordMatch.setText("✓ Las contraseñas coinciden");
                lblPasswordMatch.setTextFill(Color.web("#16A34A"));
            } else {
                lblPasswordMatch.setText("✗ Las contraseñas no coinciden");
                lblPasswordMatch.setTextFill(Color.web("#EF4444"));
            }
        }
    }

    private void updateRequirementLabel(
            Label requirementLabel,
            boolean isValid
    ) {
        if (isValid) {
            requirementLabel.setTextFill(Color.web("#16A34A"));
        } else {
            requirementLabel.setTextFill(Color.GRAY);
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