package mx.uv.internshipprogramsystem.gui.util;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;

public class ChangeViewToUpdateProjectRunnable implements Runnable {
    private final String fxmlName;
    private final ProjectDTO project;

    public ChangeViewToUpdateProjectRunnable(String fxmlName, ProjectDTO project) {
        this.fxmlName = fxmlName;
        this.project = project;
    }

    @Override
    public void run() {
        WindowManagerController.executeChangeViewToUpdateProject(fxmlName, project);
    }
}
