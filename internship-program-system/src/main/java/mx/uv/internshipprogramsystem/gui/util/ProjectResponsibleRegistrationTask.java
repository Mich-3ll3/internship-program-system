package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.managers.ProjectResponsibleManager;

public class ProjectResponsibleRegistrationTask extends Task<Boolean> {

    private final ProjectResponsibleDTO responsible;

    public ProjectResponsibleRegistrationTask(ProjectResponsibleDTO responsible) {
        this.responsible = responsible;
    }

    @Override
    protected Boolean call() throws Exception {
        ProjectResponsibleManager manager = new ProjectResponsibleManager();
        return manager.registerProjectResponsible(responsible);
    }
}
