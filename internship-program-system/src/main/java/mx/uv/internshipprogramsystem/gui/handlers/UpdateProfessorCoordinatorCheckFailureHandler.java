package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateProfessorDashboardController;

public class UpdateProfessorCoordinatorCheckFailureHandler
    implements EventHandler<WorkerStateEvent> {

    private final UpdateProfessorDashboardController controller;

    public UpdateProfessorCoordinatorCheckFailureHandler(
        UpdateProfessorDashboardController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleCoordinatorCheckFailure(exception);
    }
}
