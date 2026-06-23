package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.RegisterInternFormController;

public class InternRegistrationSuccessHandler
    implements EventHandler<WorkerStateEvent> {

    private final RegisterInternFormController controller;

    public InternRegistrationSuccessHandler(
        RegisterInternFormController controller
    ) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        controller.handleRegistrationSuccess();
    }
}
