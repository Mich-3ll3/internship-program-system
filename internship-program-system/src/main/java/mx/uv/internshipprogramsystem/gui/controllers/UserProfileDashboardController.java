package mx.uv.internshipprogramsystem.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;

public class UserProfileDashboardController {

    @FXML
    private Label lblFullName;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblRole;

    @FXML
    private void initialize() {
        UserDTO currentUser =
            UserSessionManager.getCurrentUser();

        lblFullName.setText(
            currentUser.getFullName()
        );

        lblEmail.setText(
            currentUser.getInstitutionalEmail()
        );

        lblRole.setText(
            currentUser.getRole().toString()
        );
    }

    @FXML
    private void goBack(ActionEvent event) {
        WindowManagerController.goBack();
    }
}