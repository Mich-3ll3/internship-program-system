package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterProfessorFormController;

public class ProfessorRegistrationFailureHandler
    implements EventHandler<WorkerStateEvent> {

    private final RegisterProfessorFormController controller;

    public ProfessorRegistrationFailureHandler(
        RegisterProfessorFormController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        controller.handleRegistrationFailure(exception);
    }
}
