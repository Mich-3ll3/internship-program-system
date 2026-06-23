package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import mx.uv.internshipprogramsystem.gui.controllers.SideMenuDashboardController;

public class SideMenuButtonMouseExitedHandler implements EventHandler<MouseEvent> {
    private final SideMenuDashboardController controller;
    private final Button button;

    public SideMenuButtonMouseExitedHandler(
            SideMenuDashboardController controller,
            Button button
    ) {
        this.controller = controller;
        this.button = button;
    }

    @Override
    public void handle(MouseEvent event) {
        controller.handleMouseExited(button);
    }
}
