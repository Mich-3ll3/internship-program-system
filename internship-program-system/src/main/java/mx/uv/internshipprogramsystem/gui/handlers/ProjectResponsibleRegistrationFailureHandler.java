package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.ProjectResponsibleRegisterDashboardController;

public class ProjectResponsibleRegistrationFailureHandler implements EventHandler<WorkerStateEvent> {

    private final ProjectResponsibleRegisterDashboardController controller;

    public ProjectResponsibleRegistrationFailureHandler(ProjectResponsibleRegisterDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleRegistrationFailure(exception);
    }
}
