package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.managers.LoginManager;

public class LoginDashboardController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LoginDashboardController.class);

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void handleLoginAction() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        try {
            LoginManager loginManager = new LoginManager();
            UserDTO loggedUser = loginManager.login(email, password);

            LOGGER.info(
                "Inicio de sesión exitoso para usuario con rol {}",
                loggedUser.getRole()
            );

            redirectUserByRole(loggedUser);
        } catch (BusinessException businessException) {
            LOGGER.warn(
                "Intento de inicio de sesión fallido para {}",
                email,
                businessException
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error de inicio de sesión",
                businessException.getMessage()
            );
        } catch (Exception exception) {
            LOGGER.error(
                "Error inesperado durante el inicio de sesión",
                exception
            );

            displayAlert(
                Alert.AlertType.ERROR,
                "Error de sistema",
                "No se pudo iniciar sesión."
            );
        }
    }

    private void redirectUserByRole(UserDTO user) {
        String fxmlPath = "";

        switch (user.getRole()) {
            case ADMINISTRATOR:
                fxmlPath = "AdminHomeDashboard.fxml";
                break;
            case PROFESSOR:
                fxmlPath = getProfessorHomeView(user);
                break;
            case STUDENT:
                fxmlPath = "InternHomeDashboard.fxml";
                break;
            default:
                displayAlert(
                    Alert.AlertType.WARNING,
                    "Rol inválido",
                    "El usuario no tiene un rol válido asignado."
                );
                break;
        }

        if (!fxmlPath.isEmpty()) {
            WindowManagerController.changeView(fxmlPath);
        }
    }

    private String getProfessorHomeView(UserDTO user) {
        String fxmlPath = "ProfessorHomeDashboard.fxml";

        if (user instanceof ProfessorDTO) {
            ProfessorDTO professor = (ProfessorDTO) user;

            if (professor.getIsCoordinator()) {
                fxmlPath = "CoordinatorProfessorHomeDashboard.fxml";
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

    private void displayAlert(
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