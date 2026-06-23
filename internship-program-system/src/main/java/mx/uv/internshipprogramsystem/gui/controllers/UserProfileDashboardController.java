package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class UserProfileDashboardController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileDashboardController.class);

    @FXML
    private Label lblUserInitials;

    @FXML
    private Label lblFullName;

    @FXML
    private Label lblRoleBadge;

    @FXML
    private Label lblStatusBadge;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblDetailFullName;

    @FXML
    private Label lblDetailEmail;

    @FXML
    private Label lblDetailRole;

    @FXML
    private VBox pneCodeContainer;

    @FXML
    private Label lblDetailCodeTitle;

    @FXML
    private Label lblDetailCode;

    @FXML
    private Label lblDetailStatus;

    @FXML
    private Label lblSessionLastAccess;

    @FXML
    private Label lblSessionDevice;

    @FXML
    private Label lblSessionLocation;

    @FXML
    private Label lblSessionActiveSince;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnBack;

    @FXML
    private void initialize() {
        LOGGER.info("Cargando vista de perfil de usuario.");
        populateUserData();
    }

    private void populateUserData() {
        UserDTO currentUser = UserSessionManager.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warn("No hay ningún usuario activo en sesión.");
            return;
        }

        // Initials
        String initials = "";
        String name = currentUser.getName();
        String surname = currentUser.getFirstSurname();
        if (name != null && !name.trim().isEmpty()) {
            String[] nameParts = name.trim().split("\\s+");
            if (nameParts.length > 0 && !nameParts[0].isEmpty()) {
                initials += nameParts[0].substring(0, 1).toUpperCase();
            }
        }
        if (surname != null && !surname.trim().isEmpty()) {
            String[] surnameParts = surname.trim().split("\\s+");
            if (surnameParts.length > 0 && !surnameParts[0].isEmpty()) {
                initials += surnameParts[0].substring(0, 1).toUpperCase();
            }
        }
        lblUserInitials.setText(initials);

        // General Info
        String fullName = currentUser.getFullName();
        lblFullName.setText(fullName);
        lblDetailFullName.setText(fullName);

        String email = currentUser.getInstitutionalEmail();
        lblEmail.setText(email);
        lblDetailEmail.setText(email);

        // Badges and Role
        String roleText = "Usuario";

        if (currentUser.getRole() != null) {
            switch (currentUser.getRole()) {
                case ADMINISTRATOR:
                    roleText = "Administrador";
                    lblRoleBadge.setStyle("-fx-background-color: #e0ecff; -fx-text-fill: #1d4ed8; -fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 4 10 4 10; -fx-background-radius: 12;");
                    break;
                case PROFESSOR:
                    if (currentUser instanceof ProfessorDTO professor && professor.getIsCoordinator()) {
                        roleText = "Coordinador";
                    } else {
                        roleText = "Profesor";
                    }
                    lblRoleBadge.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #d97706; -fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 4 10 4 10; -fx-background-radius: 12;");
                    break;
                case STUDENT:
                    roleText = "Estudiante";
                    lblRoleBadge.setStyle("-fx-background-color: #f3e8ff; -fx-text-fill: #7c3aed; -fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 4 10 4 10; -fx-background-radius: 12;");
                    break;
            }
        }
        lblRoleBadge.setText(roleText);
        lblDetailRole.setText(roleText);

        // Account Status
        boolean isActive = currentUser.getIsActive() != null && currentUser.getIsActive();
        if (isActive) {
            lblStatusBadge.setText("● Activo");
            lblStatusBadge.setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #065f46; -fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 4 10 4 10; -fx-background-radius: 12;");
            lblDetailStatus.setText("Activa");
        } else {
            lblStatusBadge.setText("● Inactivo");
            lblStatusBadge.setStyle("-fx-background-color: #fef2f2; -fx-text-fill: #991b1b; -fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 4 10 4 10; -fx-background-radius: 12;");
            lblDetailStatus.setText("Inactiva");
        }

        // Code (Staff Number / Enrollment)
        if (currentUser instanceof ProfessorDTO professor) {
            lblDetailCodeTitle.setText("NÚMERO DE PERSONAL");
            lblDetailCode.setText(professor.getStaffNumber());
            pneCodeContainer.setVisible(true);
            pneCodeContainer.setManaged(true);
        } else if (currentUser instanceof InternDTO intern) {
            lblDetailCodeTitle.setText("MATRÍCULA");
            lblDetailCode.setText(intern.getEnrollmentNumber());
            pneCodeContainer.setVisible(true);
            pneCodeContainer.setManaged(true);
        } else {
            pneCodeContainer.setVisible(false);
            pneCodeContainer.setManaged(false);
        }

        // Session Information
        // Last Access (formatted date-time)
        Locale localeEs = new Locale("es", "MX");
        DateTimeFormatter lastAccessFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy — HH:mm", localeEs);
        LocalDateTime lastAccessTime = LocalDateTime.now();
        if (currentUser.getLoginLockDate() != null) {
            lastAccessTime = currentUser.getLoginLockDate().toLocalDateTime();
        }
        String formattedLastAccess = lastAccessTime.format(lastAccessFormatter);
        if (formattedLastAccess != null && !formattedLastAccess.isEmpty()) {
            formattedLastAccess = formattedLastAccess.substring(0, 1).toUpperCase() + formattedLastAccess.substring(1);
        }
        lblSessionLastAccess.setText(formattedLastAccess);

        // Device
        String osName = System.getProperty("os.name");
        lblSessionDevice.setText(osName + " (Cliente de Escritorio JavaFX)");

        // Location
        lblSessionLocation.setText("Coatepec, Veracruz");

        // Active since
        Instant startTime = UserSessionManager.getSessionStartTime();
        if (startTime != null) {
            Duration elapsed = Duration.between(startTime, Instant.now());
            long minutes = elapsed.toMinutes();
            DateTimeFormatter activeSinceFormatter = DateTimeFormatter.ofPattern("HH:mm", localeEs);
            String timeString = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault()).format(activeSinceFormatter);
            if (minutes <= 0) {
                lblSessionActiveSince.setText(timeString + " — hace un momento");
            } else if (minutes == 1) {
                lblSessionActiveSince.setText(timeString + " — hace 1 minuto");
            } else {
                lblSessionActiveSince.setText(timeString + " — hace " + minutes + " min");
            }
        } else {
            lblSessionActiveSince.setText("Reciente");
        }
    }

    @FXML
    private void handleBtnLogoutClick() {
        LOGGER.info("Cerrando sesión del usuario actual.");
        UserSessionManager.clearSession();
        WindowManagerController.changeView("LoginDashboard.fxml");
    }

    @FXML
    private void handleBtnBackClick() {
        LOGGER.info("Regresando a la vista anterior.");
        WindowManagerController.goBack();
    }
}
