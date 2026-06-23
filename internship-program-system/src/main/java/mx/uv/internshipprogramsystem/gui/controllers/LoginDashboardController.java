package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.LoginManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class LoginDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            LoginDashboardController.class
        );

    private static final String EYE_OPEN_IMAGE_PATH =
        "/images/eye-open.png";

    private static final String EYE_CLOSED_IMAGE_PATH =
        "/images/eye-closed.png";

    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MAX_PASSWORD_LENGTH = 64;

    @FXML
    private VBox vboxForm;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField pwdPassword;

    @FXML
    private TextField txtVisiblePassword;

    @FXML
    private Label lblEmailError;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnTogglePasswordVisibility;

    @FXML
    private ImageView imgPasswordVisibility;

    private boolean isPasswordVisible;
    private boolean isTogglingVisibility;

    @FXML
    private void initialize() {
        setPasswordVisibilityIcon(
            EYE_OPEN_IMAGE_PATH
        );
        btnLogin.setDisable(true);

        // Character length limitations
        limitTextFieldLength(txtEmail, MAX_EMAIL_LENGTH);
        limitTextFieldLength(pwdPassword, MAX_PASSWORD_LENGTH);
        limitTextFieldLength(txtVisiblePassword, MAX_PASSWORD_LENGTH);

        // Real-time validation listeners
        txtEmail.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.LoginInputValidationListener(this)
        );
        pwdPassword.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.LoginInputValidationListener(this)
        );
        txtVisiblePassword.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.LoginInputValidationListener(this)
        );

        // Press and hold logic for eye icon
        mx.uv.internshipprogramsystem.gui.handlers.PasswordVisibilityMouseHandler mouseHandler =
            new mx.uv.internshipprogramsystem.gui.handlers.PasswordVisibilityMouseHandler(this);
        btnTogglePasswordVisibility.setOnMousePressed(mouseHandler);
        btnTogglePasswordVisibility.setOnMouseReleased(mouseHandler);
        btnTogglePasswordVisibility.setOnMouseExited(mouseHandler);

        // Enter key action listeners on inputs to trigger login
        mx.uv.internshipprogramsystem.gui.handlers.LoginActionTriggerHandler actionHandler =
            new mx.uv.internshipprogramsystem.gui.handlers.LoginActionTriggerHandler(this);
        txtEmail.setOnAction(actionHandler);
        pwdPassword.setOnAction(actionHandler);
        txtVisiblePassword.setOnAction(actionHandler);
    }

    private void limitTextFieldLength(TextField textField, int maxLength) {
        textField.textProperty().addListener(
            new mx.uv.internshipprogramsystem.gui.handlers.TextFieldLengthLimiterListener(textField, maxLength)
        );
    }

    public void handleShowPassword() {
        showPassword();
    }

    public void handleHidePassword() {
        hidePassword();
    }

    public void handleMouseExited() {
        if (isPasswordVisible) {
            hidePassword();
        }
    }

    public void handleValidateInputs() {
        validateInputs();
    }

    private void showPassword() {
        isTogglingVisibility = true;
        try {
            isPasswordVisible = true;

            txtVisiblePassword.setText(
                pwdPassword.getText()
            );

            txtVisiblePassword.setVisible(true);
            txtVisiblePassword.setManaged(true);
            pwdPassword.setVisible(false);
            pwdPassword.setManaged(false);

            setPasswordVisibilityIcon(
                EYE_CLOSED_IMAGE_PATH
            );

            txtVisiblePassword.requestFocus();
            txtVisiblePassword.selectEnd();
        } finally {
            isTogglingVisibility = false;
        }
        validateInputs();
    }

    private void hidePassword() {
        isTogglingVisibility = true;
        try {
            isPasswordVisible = false;

            pwdPassword.setText(
                txtVisiblePassword.getText()
            );

            txtVisiblePassword.clear(); // clear visible password from memory

            pwdPassword.setVisible(true);
            pwdPassword.setManaged(true);
            txtVisiblePassword.setVisible(false);
            txtVisiblePassword.setManaged(false);

            setPasswordVisibilityIcon(
                EYE_OPEN_IMAGE_PATH
            );

            pwdPassword.requestFocus();
            pwdPassword.selectEnd();
        } finally {
            isTogglingVisibility = false;
        }
        validateInputs();
    }

    private void setPasswordVisibilityIcon(
            String imagePath
    ) {
        java.io.InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream != null) {
            Image image = new Image(stream);
            imgPasswordVisibility.setImage(image);
        } else {
            LOGGER.warn(
                "No se encontró la imagen del botón de contraseña: {}",
                imagePath
            );
        }
    }

    @FXML
    public void handleBtnLoginClick() {
        if (btnLogin.isDisable()) {
            return;
        }

        FormAlertSupport.clearFieldErrorsFromActiveWindow();

        String email = txtEmail.getText();
        String password = getCurrentPassword();

        mx.uv.internshipprogramsystem.gui.util.LoginTask loginTask =
            new mx.uv.internshipprogramsystem.gui.util.LoginTask(email, password);

        loginTask.setOnSucceeded(
            new mx.uv.internshipprogramsystem.gui.handlers.LoginSuccessHandler(this, loginTask)
        );

        loginTask.setOnFailed(
            new mx.uv.internshipprogramsystem.gui.handlers.LoginFailureHandler(this, loginTask)
        );

        setFieldsDisable(true);

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    public void handleLoginSuccess(UserDTO loggedUser) {
        UserSessionManager.setCurrentUser(loggedUser);

        LOGGER.info(
            "Inicio de sesion exitoso para usuario con rol {}",
            loggedUser.getRole()
        );

        pwdPassword.setText("");
        txtVisiblePassword.setText("");

        String fxmlPath = loggedUser.getRole().getFxmlPath(loggedUser);
        WindowManagerController.changeView(fxmlPath);
    }

    public void handleLoginFailure(Throwable exception) {
        String email = txtEmail.getText();
        if (exception instanceof java.util.concurrent.ExecutionException && exception.getCause() != null) {
            exception = exception.getCause();
        }

        if (exception instanceof DataAccessException || (exception != null && exception.getCause() instanceof DataAccessException)) {
            LOGGER.error("No se pudo procesar el inicio de sesion debido a un problema con la base de datos.", exception);
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("Servicio No Disponible");
            alert.setContentText("No se pudo establecer comunicación con el sistema central. Por favor, verifique su conexión a internet o intente más tarde.");
            alert.showAndWait();
        } else {
            String errorMessage = "No se pudo iniciar sesión.";
            if (exception instanceof BusinessException) {
                errorMessage = exception.getMessage();
                LOGGER.warn(
                    "Intento de inicio de sesion fallido para el usuario {}: {}",
                    email,
                    errorMessage
                );
            } else {
                LOGGER.error(
                    "Error inesperado durante el inicio de sesion para el usuario {}",
                    email,
                    exception
                );
            }

            FormAlertSupport.showError(
                "Error de inicio de sesión",
                errorMessage
            );
        }

        runShakeAnimation();

        setFieldsDisable(false);
        validateInputs();
    }

    private void runShakeAnimation() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(50), vboxForm);
        transition.setFromX(0);
        transition.setByX(10);
        transition.setCycleCount(6);
        transition.setAutoReverse(true);
        transition.play();
    }

    private void validateInputs() {
        if (isTogglingVisibility) {
            return;
        }

        try {
            FormAlertSupport.clearFieldError(txtEmail);
            FormAlertSupport.clearFieldError(pwdPassword);
            FormAlertSupport.clearFieldError(txtVisiblePassword);

            String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
            String password = getCurrentPassword();

            boolean isEmailValid = false;
            if (email.isEmpty()) {
                txtEmail.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-border-color: #D1D5DB; -fx-border-radius: 8; -fx-padding: 0 10 0 10;");
                lblEmailError.setVisible(false);
                lblEmailError.setManaged(false);
            } else if (email.matches("^[A-Za-z0-9+_.-]+@(uv\\.mx|estudiantes\\.uv\\.mx)$")) {
                txtEmail.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-border-color: #10B981; -fx-border-radius: 8; -fx-padding: 0 10 0 10;");
                lblEmailError.setVisible(false);
                lblEmailError.setManaged(false);
                isEmailValid = true;
            } else {
                txtEmail.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-border-color: #EF4444; -fx-border-radius: 8; -fx-padding: 0 10 0 10;");
                lblEmailError.setText("El correo debe ser institucional (@uv.mx o @estudiantes.uv.mx).");
                lblEmailError.setVisible(true);
                lblEmailError.setManaged(true);
            }

            boolean isPasswordNotEmpty = password != null && !password.trim().isEmpty();
            LOGGER.info("Validación de entradas - ¿Es correo válido?: {}, ¿Contraseña no vacía?: {}, correo: '{}'", isEmailValid, isPasswordNotEmpty, email);
            btnLogin.setDisable(!(isEmailValid && isPasswordNotEmpty));
        } catch (Throwable t) {
            LOGGER.error("Excepción en validateInputs", t);
        }
    }

    private void setFieldsDisable(boolean disable) {
        txtEmail.setDisable(disable);
        pwdPassword.setDisable(disable);
        txtVisiblePassword.setDisable(disable);
        btnLogin.setDisable(disable);
        btnTogglePasswordVisibility.setDisable(disable);
    }

    private String getCurrentPassword() {
        String password;

        if (isPasswordVisible) {
            password =
                txtVisiblePassword.getText();
        } else {
            password =
                pwdPassword.getText();
        }

        return password;
    }

    @FXML
    private void handleOpenActivationView() {
        WindowManagerController.changeView(
            "ActivateAccountDashboard.fxml"
        );
    }

    @FXML
    private void handleOpenForgotPasswordView() {
        WindowManagerController.changeView(
            "ForgotPasswordDashboard.fxml"
        );
    }
}