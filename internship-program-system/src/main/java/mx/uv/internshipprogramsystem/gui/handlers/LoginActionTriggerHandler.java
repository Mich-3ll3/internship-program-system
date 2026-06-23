package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.LoginDashboardController;

public class LoginActionTriggerHandler implements EventHandler<ActionEvent> {
    private final LoginDashboardController controller;

    public LoginActionTriggerHandler(LoginDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {
        controller.handleBtnLoginClick();
    }
}
