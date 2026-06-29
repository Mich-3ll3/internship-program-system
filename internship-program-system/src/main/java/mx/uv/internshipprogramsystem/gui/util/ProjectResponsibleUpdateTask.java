package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.managers.ProjectResponsibleManager;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

public class ProjectResponsibleUpdateTask extends Task<Boolean> {
    
    private final ProjectResponsibleDTO responsible;
    private final ProjectResponsibleManager manager;
    
    public ProjectResponsibleUpdateTask(ProjectResponsibleDTO responsible) {
        this.responsible = responsible;
        this.manager = new ProjectResponsibleManager();
    }
    
    @Override
    protected Boolean call() throws mx.uv.internshipprogramsystem.logic.exceptions.BusinessException, mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException {
        return manager.updateProjectResponsible(responsible);
    }
}
