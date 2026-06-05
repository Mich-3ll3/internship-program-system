package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.LoginManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class LoginDashboardController {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            LoginDashboardController.class
        );

    private static final String EYE_OPEN_IMAGE_PATH =
        "images/eye-open.png";

    private static final String EYE_CLOSED_IMAGE_PATH =
        "images/eye-closed.png";

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField pwdPassword;

    @FXML
    private TextField txtVisiblePassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnTogglePasswordVisibility;

    @FXML
    private ImageView imgPasswordVisibility;

    private boolean isPasswordVisible;

    @FXML
    private void initialize() {
        setPasswordVisibilityIcon(
            EYE_OPEN_IMAGE_PATH
        );
    }

    @FXML
    private void handleTogglePasswordVisibility() {
        if (!isPasswordVisible) {
            txtVisiblePassword.setText(
                pwdPassword.getText()
            );

            txtVisiblePassword.setVisible(
                true
            );

            txtVisiblePassword.setManaged(
                true
            );

            pwdPassword.setVisible(
                false
            );

            pwdPassword.setManaged(
                false
            );

            setPasswordVisibilityIcon(
                EYE_CLOSED_IMAGE_PATH
            );

            isPasswordVisible =
                true;
        } else {
            pwdPassword.setText(
                txtVisiblePassword.getText()
            );

            pwdPassword.setVisible(
                true
            );

            pwdPassword.setManaged(
                true
            );

            txtVisiblePassword.setVisible(
                false
            );

            txtVisiblePassword.setManaged(
                false
            );

            setPasswordVisibilityIcon(
                EYE_OPEN_IMAGE_PATH
            );

            isPasswordVisible =
                false;
        }
    }

    private void setPasswordVisibilityIcon(
            String imagePath
    ) {
        if (getClass().getClassLoader().getResourceAsStream(
                imagePath
        ) != null) {
            Image image =
                new Image(
                    getClass().getClassLoader().getResourceAsStream(
                        imagePath
                    )
                );

            imgPasswordVisibility.setImage(
                image
            );
        } else {
            LOGGER.warn(
                "No se encontró la imagen del botón de contraseña: {}",
                imagePath
            );
        }
    }

    @FXML
    private void handleLoginAction() {
        if (btnLogin.isDisable()) {
            return;
        }

        btnLogin.setDisable(
            true
        );

        String email =
            txtEmail.getText();

        String password =
            getCurrentPassword();

        try {
            LoginManager loginManager =
                new LoginManager();

            UserDTO loggedUser =
                loginManager.login(
                    email,
                    password
                );

            UserSessionManager.setCurrentUser(
                loggedUser
            );

            LOGGER.info(
                "Inicio de sesión exitoso para usuario con rol {}",
                loggedUser.getRole()
            );

            redirectUserByRole(
                loggedUser
            );
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Intento de inicio de sesión fallido para {}",
                email,
                businessException
            );

            FormAlertSupport.showError(
                "Error de inicio de sesión",
                businessException.getMessage()
            );

            btnLogin.setDisable(
                false
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado durante el inicio de sesión",
                exception
            );

            FormAlertSupport.showError(
                "Error de sistema",
                "No se pudo iniciar sesión."
            );

            btnLogin.setDisable(
                false
            );
        }
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

    private void redirectUserByRole(
            UserDTO user
    ) {
        String fxmlPath =
            "";

        switch (user.getRole()) {
            case ADMINISTRATOR:
                fxmlPath =
                    "AdminHomeDashboard.fxml";
                break;

            case PROFESSOR:
                fxmlPath =
                    getProfessorHomeView(
                        user
                    );
                break;

            case STUDENT:
                fxmlPath =
                    "InternHomeDashboard.fxml";
                break;

            default:
                FormAlertSupport.showWarning(
                    "Rol inválido",
                    "El usuario no tiene un rol válido asignado."
                );
                break;
        }

        if (!fxmlPath.isEmpty()) {
            WindowManagerController.changeView(
                fxmlPath
            );
        }
    }

    private String getProfessorHomeView(
            UserDTO user
    ) {
        String fxmlPath =
            "ProfessorHomeDashboard.fxml";

        if (user instanceof ProfessorDTO) {
            ProfessorDTO professor =
                (ProfessorDTO) user;

            if (professor.getIsCoordinator()) {
                fxmlPath =
                    "CoordinatorProfessorHomeDashboard.fxml";
            }
        }

        return fxmlPath;
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