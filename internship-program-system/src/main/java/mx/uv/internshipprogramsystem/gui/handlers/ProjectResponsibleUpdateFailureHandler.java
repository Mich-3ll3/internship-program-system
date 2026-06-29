package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.ProjectResponsibleUpdateDashboardController;

public class ProjectResponsibleUpdateFailureHandler implements EventHandler<WorkerStateEvent> {

    private final ProjectResponsibleUpdateDashboardController controller;

    public ProjectResponsibleUpdateFailureHandler(ProjectResponsibleUpdateDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleUpdateFailure(exception);
    }
}
