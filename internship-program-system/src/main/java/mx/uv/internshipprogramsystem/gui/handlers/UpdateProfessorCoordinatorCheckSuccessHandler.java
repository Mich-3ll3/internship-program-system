package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateProfessorDashboardController;

public class UpdateProfessorCoordinatorCheckSuccessHandler
    implements EventHandler<WorkerStateEvent> {

    private final UpdateProfessorDashboardController controller;

    public UpdateProfessorCoordinatorCheckSuccessHandler(
        UpdateProfessorDashboardController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Boolean exists = (Boolean) event.getSource().getValue();
        controller.handleCoordinatorCheckSuccess(
            exists != null && exists
        );
    }
}
