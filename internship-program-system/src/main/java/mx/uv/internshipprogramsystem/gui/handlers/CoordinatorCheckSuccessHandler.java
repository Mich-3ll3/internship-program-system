package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterProfessorFormController;

public class CoordinatorCheckSuccessHandler
    implements EventHandler<WorkerStateEvent> {

    private final RegisterProfessorFormController controller;

    public CoordinatorCheckSuccessHandler(
        RegisterProfessorFormController controller
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
