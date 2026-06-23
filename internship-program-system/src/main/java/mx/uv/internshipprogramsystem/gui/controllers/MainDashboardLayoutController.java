package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class MainDashboardLayoutController {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MainDashboardLayoutController.class);

    @FXML
    private BorderPane pneContentPane;

    @FXML
    private HBox pneHeaderBar;

    @FXML
    private Label lblDashboardTitle;

    @FXML
    private Label lblWelcomeMessage;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblUserInitials;

    @FXML
    private StackPane pneAvatarContainer;

    @FXML
    private StackPane pneViewContainer;

    @FXML
    private void initialize() {
        initializeDate();
        initializeProfile();
    }

    private void initializeDate() {
        java.time.format.DateTimeFormatter formatter =
            java.time.format.DateTimeFormatter.ofPattern(
                "EEEE, d 'de' MMMM 'de' yyyy",
                new java.util.Locale("es", "MX")
            );
        String formattedDate = java.time.LocalDate.now().format(formatter);
        if (formattedDate != null && !formattedDate.isEmpty()) {
            formattedDate = formattedDate.substring(0, 1).toUpperCase()
                + formattedDate.substring(1);
        }
        lblDate.setText(formattedDate);
    }

    private void initializeProfile() {
        UserDTO currentUser = UserSessionManager.getCurrentUser();
        if (currentUser != null) {
            lblWelcomeMessage.setText("Bienvenido/a, " + currentUser.getFullName());
            updateHeaderVisibility(true);

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
        }
    }

    @FXML
    private void handlePneAvatarClick(javafx.scene.input.MouseEvent event) {
        WindowManagerController.changeView("UserProfileDashboard.fxml");
    }

    public void updateHeaderVisibility(boolean isHome) {
        if (pneContentPane != null && pneHeaderBar != null) {
            if (isHome) {
                pneContentPane.setTop(pneHeaderBar);
                pneHeaderBar.setVisible(true);
                pneHeaderBar.setManaged(true);
            } else {
                pneContentPane.setTop(null);
            }
        } else if (pneHeaderBar != null) {
            pneHeaderBar.setVisible(isHome);
            pneHeaderBar.setManaged(isHome);
        }

        UserDTO currentUser = UserSessionManager.getCurrentUser();
        if (isHome && currentUser != null && currentUser.getRole() == mx.uv.internshipprogramsystem.logic.dto.UserRole.ADMINISTRATOR) {
            lblDashboardTitle.setText("Panel de Administración");
            lblDashboardTitle.setVisible(true);
            lblDashboardTitle.setManaged(true);
        } else {
            lblDashboardTitle.setVisible(false);
            lblDashboardTitle.setManaged(false);
        }
    }

    public StackPane getPneViewContainer() {
        return pneViewContainer;
    }
}
