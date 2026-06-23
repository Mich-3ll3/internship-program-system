package mx.uv.internshipprogramsystem.gui.util;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;

public class ChangeViewToUpdateProfessorRunnable implements Runnable {
    private final String fxmlName;
    private final ProfessorDTO professor;

    public ChangeViewToUpdateProfessorRunnable(String fxmlName, ProfessorDTO professor) {
        this.fxmlName = fxmlName;
        this.professor = professor;
    }

    @Override
    public void run() {
        WindowManagerController.executeChangeViewToUpdateProfessor(fxmlName, professor);
    }
}
