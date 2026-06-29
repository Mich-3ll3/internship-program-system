package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.UpdateInternDashboardController;

public class InternUpdateSuccessHandler implements EventHandler<WorkerStateEvent> {

    private final UpdateInternDashboardController controller;

    public InternUpdateSuccessHandler(UpdateInternDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Boolean success = (Boolean) event.getSource().getValue();
        controller.handleUpdateSuccess(success);
    }
}
