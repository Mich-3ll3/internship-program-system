package mx.uv.internshipprogramsystem.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;

public class LoginDashboardController {

    private static final Logger LOGGER = Logger.getLogger(LoginDashboardController.class.getName());

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblEmailError;
    @FXML private Label lblMinLengthRequirement;     
    @FXML private Label lblUppercaseRequirement;     
    @FXML private Label lblNumberRequirement;       
    @FXML private Label lblSymbolRequirement;   
    @FXML private Pane strengthBarPart1;
    @FXML private Pane strengthBarPart2;
    @FXML private Pane strengthBarPart3;
    @FXML private Pane strengthBarPart4;
    private final String SUCCESS_COLOR_HEX = "#5cb85c";
    private final String DEFAULT_GRAY_HEX = "#E0E0E0";
    private final String WARNING_TEXT_HEX = "#757575";

    @FXML
    public void initialize() {
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            validateInstitutionalEmail(newVal);
        });

        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordStrengthUI(newVal == null ? "" : newVal);
        });
    }

    private void validateInstitutionalEmail(String email) {
        String institutionalRegex = "^[A-Za-z0-9+_.-]+@(uv\\.mx|estudiantes\\.uv\\.mx)$";
        if (email != null && !email.matches(institutionalRegex)) {
            lblEmailError.setText("Please enter a valid institutional email");
            lblEmailError.setVisible(true);
        } else {
            lblEmailError.setVisible(false);
        }
    }

    private void updatePasswordStrengthUI(String password) {
        boolean hasMinLength = password.length() >= 8;
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[@#$%^&+=!].*");

        lblMinLengthRequirement.setTextFill(Color.web(hasMinLength ? SUCCESS_COLOR_HEX : WARNING_TEXT_HEX));
        lblUppercaseRequirement.setTextFill(Color.web(hasUppercase ? SUCCESS_COLOR_HEX : WARNING_TEXT_HEX));
        lblNumberRequirement.setTextFill(Color.web(hasNumber ? SUCCESS_COLOR_HEX : WARNING_TEXT_HEX));
        lblSymbolRequirement.setTextFill(Color.web(hasSymbol ? SUCCESS_COLOR_HEX : WARNING_TEXT_HEX));

        int strengthPoints = 0;
        if (hasMinLength) strengthPoints++;
        if (hasUppercase) strengthPoints++;
        if (hasNumber) strengthPoints++;
        if (hasSymbol) strengthPoints++;

        updateStrengthBarIndicator(strengthBarPart1, strengthPoints >= 1);
        updateStrengthBarIndicator(strengthBarPart2, strengthPoints >= 2);
        updateStrengthBarIndicator(strengthBarPart3, strengthPoints >= 3);
        updateStrengthBarIndicator(strengthBarPart4, strengthPoints >= 4);
    }

    private void updateStrengthBarIndicator(Pane barSegment, boolean isActivated) {
        if (barSegment != null) {
            String color = isActivated ? SUCCESS_COLOR_HEX : DEFAULT_GRAY_HEX;
            barSegment.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
        }
    }

    @FXML
    private void handleLoginAction() {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        try {
            UserDAO userDAO = new UserDAO();
            UserDTO loggedUser = userDAO.login(email, password); 

            if (loggedUser != null && loggedUser.getId() > 0) {
                redirectUserByRole(loggedUser);
                LOGGER.info("Successful login for: " + loggedUser.getRol());
            } else {
                displayAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login process", e);
            displayAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred. Please contact support.");
        }
    }
    
    private void redirectUserByRole(UserDTO user) {
        String fxmlPath = "";

        switch (user.getRol()) {
            case ADMINISTRADOR:
                fxmlPath = "AdminHomeDashboard.fxml";
                break;
            case PROFESOR:
                if (user instanceof ProfessorDTO) {
                    ProfessorDTO professor = (ProfessorDTO) user;
                    fxmlPath = professor.getIsCoordinator() 
                               ? "CoordinatorProfessorHomeDashboard.fxml" 
                               : "ProfessorHomeDashboard.fxml";
                } else {
                    fxmlPath = "ProfessorHomeDashboard.fxml";
                }
                break;
            case ESTUDIANTE:
                fxmlPath = "InternHomeDashboard.fxml";
                break;
            default:
                displayAlert(Alert.AlertType.WARNING, "Role Error", "The user does not have a valid role assigned.");
                return;
        }
        
        WindowManagerController.changeView(fxmlPath);
    }

    private void displayAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}