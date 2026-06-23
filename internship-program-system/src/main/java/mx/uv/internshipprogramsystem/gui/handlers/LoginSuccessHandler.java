package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.LoginDashboardController;
import mx.uv.internshipprogramsystem.gui.util.LoginTask;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;

public class LoginSuccessHandler implements EventHandler<WorkerStateEvent> {
    private final LoginDashboardController controller;
    private final LoginTask task;

    public LoginSuccessHandler(LoginDashboardController controller, LoginTask task) {
        this.controller = controller;
        this.task = task;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        UserDTO loggedUser = task.getValue();
        controller.handleLoginSuccess(loggedUser);
    }
}
