package mx.uv.internshipprogramsystem.gui.util;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;

public class ChangeViewToUpdateInternRunnable implements Runnable {
    private final String fxmlName;
    private final InternDTO intern;

    public ChangeViewToUpdateInternRunnable(String fxmlName, InternDTO intern) {
        this.fxmlName = fxmlName;
        this.intern = intern;
    }

    @Override
    public void run() {
        WindowManagerController.executeChangeViewToUpdateIntern(fxmlName, intern);
    }
}
