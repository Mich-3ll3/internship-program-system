package mx.uv.internshipprogramsystem.gui.handlers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import mx.uv.internshipprogramsystem.gui.controllers.WindowManagerController;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;
import mx.uv.internshipprogramsystem.logic.managers.AccessControlManager;
import mx.uv.internshipprogramsystem.logic.managers.UserSessionManager;
import mx.uv.internshipprogramsystem.logic.security.Permission;
import mx.uv.internshipprogramsystem.gui.util.FormAlertSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationActionHandler implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationActionHandler.class);

    private final String fxmlName;
    private final Permission requiredPermission;

    public NavigationActionHandler(String fxmlName) {
        this.fxmlName = fxmlName;
        this.requiredPermission = null;
    }

    public NavigationActionHandler(String fxmlName, Permission requiredPermission) {
        this.fxmlName = fxmlName;
        this.requiredPermission = requiredPermission;
    }

    @Override
    public void handle(ActionEvent event) {
        if (requiredPermission != null) {
            try {
                AccessControlManager accessControlManager = new AccessControlManager();
                accessControlManager.validatePermission(
                    UserSessionManager.getCurrentUser(),
                    requiredPermission
                );
            } catch (BusinessException businessException) {
                LOGGER.warn("Acceso denegado al modulo: {}", fxmlName, businessException);
                FormAlertSupport.showError("Acceso denegado", businessException.getMessage());
                return;
            } catch (DataAccessException dataAccessException) {
                LOGGER.error("Error de conexion al validar permisos.", dataAccessException);
                FormAlertSupport.showError(
                    "Error de conexion",
                    "No se pudo conectar con la base de datos para verificar sus permisos."
                );
                return;
            }
        }

        if ("goBack".equalsIgnoreCase(fxmlName)) {
            WindowManagerController.goBack();
        } else {
            WindowManagerController.changeView(fxmlName);
        }
    }
}
