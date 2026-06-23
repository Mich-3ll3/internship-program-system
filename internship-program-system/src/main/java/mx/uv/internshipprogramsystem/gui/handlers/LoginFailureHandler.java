package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.LoginDashboardController;
import mx.uv.internshipprogramsystem.gui.util.LoginTask;

public class LoginFailureHandler implements EventHandler<WorkerStateEvent> {
    private final LoginDashboardController controller;
    private final LoginTask task;

    public LoginFailureHandler(LoginDashboardController controller, LoginTask task) {
        this.controller = controller;
        this.task = task;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = task.getException();
        controller.handleLoginFailure(exception);
    }
}
