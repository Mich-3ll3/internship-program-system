package mx.uv.internshipprogramsystem.gui.handlers;

import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GlobalNavigationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalNavigationHandler.class);

    private GlobalNavigationHandler() {
    }

    public static void goBack() {
        WindowManagerController.goBack();
    }

    public static void changeView(String fxmlName) {
        WindowManagerController.changeView(fxmlName);
    }

    public static void logOut() {
        UserSessionManager.clearSession();
        LOGGER.info("Cierre de sesion realizado correctamente.");
        WindowManagerController.changeView("LoginDashboard.fxml");
    }
}
