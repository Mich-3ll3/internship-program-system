package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.ProjectResponsibleRegisterDashboardController;

public class ProjectResponsibleRegistrationSuccessHandler implements EventHandler<WorkerStateEvent> {

    private final ProjectResponsibleRegisterDashboardController controller;

    public ProjectResponsibleRegistrationSuccessHandler(ProjectResponsibleRegisterDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Boolean success = (Boolean) event.getSource().getValue();
        if (Boolean.TRUE.equals(success)) {
            controller.handleRegistrationSuccess();
        }
    }
}
