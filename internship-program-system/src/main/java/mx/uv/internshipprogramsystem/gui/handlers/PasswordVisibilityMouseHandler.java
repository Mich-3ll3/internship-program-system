package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import mx.uv.internshipprogramsystem.gui.controllers.LoginDashboardController;

public class PasswordVisibilityMouseHandler implements EventHandler<MouseEvent> {
    private final LoginDashboardController controller;

    public PasswordVisibilityMouseHandler(LoginDashboardController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            controller.handleShowPassword();
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            controller.handleHidePassword();
        } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
            controller.handleMouseExited();
        }
    }
}
