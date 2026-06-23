package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterProfessorFormController;

public class ProfessorRegistrationSuccessHandler
    implements EventHandler<WorkerStateEvent> {

    private final RegisterProfessorFormController controller;

    public ProfessorRegistrationSuccessHandler(
        RegisterProfessorFormController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        controller.handleRegistrationSuccess();
    }
}
