package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateInternDashboardController;

public class InternUpdateFailureHandler implements EventHandler<WorkerStateEvent> {

    private final UpdateInternDashboardController controller;

    public InternUpdateFailureHandler(UpdateInternDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleUpdateFailure(exception);
    }
}
