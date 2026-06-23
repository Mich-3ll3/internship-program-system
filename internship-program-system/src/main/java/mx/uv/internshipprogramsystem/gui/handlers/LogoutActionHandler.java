package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutActionHandler implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutActionHandler.class);

    @Override
    public void handle(ActionEvent event) {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesion realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}
