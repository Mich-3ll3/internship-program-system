package mx.uv.internshipprogramsystem.gui.util;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;

public class ChangeViewRunnable implements Runnable {
    private final String fxmlName;

    public ChangeViewRunnable(String fxmlName) {
        this.fxmlName = fxmlName;
    }

    @Override
    public void run() {
        WindowManagerController.executeChangeView(fxmlName);
    }
}
