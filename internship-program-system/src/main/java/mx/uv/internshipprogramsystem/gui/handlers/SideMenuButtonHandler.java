package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mx.uv.internshipprogramsystem.gui.controllers.SideMenuDashboardController;
import mx.uv.internshipprogramsystem.logic.security.MenuOption;

public class SideMenuButtonHandler implements EventHandler<ActionEvent> {
    private final SideMenuDashboardController controller;
    private final Button button;
    private final MenuOption option;

    public SideMenuButtonHandler(
            SideMenuDashboardController controller,
            Button button,
            MenuOption option
    ) {
        this.controller = controller;
        this.button = button;
        this.option = option;
    }

    @Override
    public void handle(ActionEvent event) {
        controller.handleBtnOptionClick(button, option);
    }
}
