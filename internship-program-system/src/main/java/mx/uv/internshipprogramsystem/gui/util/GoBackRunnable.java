package mx.uv.internshipprogramsystem.gui.util;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;

public class GoBackRunnable implements Runnable {
    @Override
    public void run() {
        WindowManagerController.executeGoBack();
    }
}
