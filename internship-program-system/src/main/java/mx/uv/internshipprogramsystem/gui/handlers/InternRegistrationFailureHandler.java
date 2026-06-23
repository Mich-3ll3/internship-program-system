package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterInternFormController;

public class InternRegistrationFailureHandler
    implements EventHandler<WorkerStateEvent> {

    private final RegisterInternFormController controller;

    public InternRegistrationFailureHandler(
        RegisterInternFormController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleRegistrationFailure(exception);
    }
}
