package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.ProjectResponsibleUpdateDashboardController;

public class ProjectResponsibleUpdateSuccessHandler implements EventHandler<WorkerStateEvent> {

    private final ProjectResponsibleUpdateDashboardController controller;

    public ProjectResponsibleUpdateSuccessHandler(ProjectResponsibleUpdateDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        Boolean success = (Boolean) event.getSource().getValue();
        if (success != null && success) {
            controller.handleUpdateSuccess();
        } else {
            controller.handleUpdateFailure(new Exception("Error desconocido al actualizar el responsable."));
        }
    }
}
