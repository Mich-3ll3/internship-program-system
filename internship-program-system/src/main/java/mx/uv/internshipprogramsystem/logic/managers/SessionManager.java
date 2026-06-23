package mx.uv.internshipprogramsystem.logic.managers;

import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public final class SessionManager {
    private static SessionManager instance;
    private final AccessControlManager accessControlManager = new AccessControlManager();

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public UserDTO getCurrentUser() {
        return UserSessionManager.getCurrentUser();
    }

    public List<Permission> getPermissions() {
        UserDTO user = getCurrentUser();
        List<Permission> permissions = new ArrayList<>();
        if (user != null) {
            for (Permission p : Permission.values()) {
                if (accessControlManager.hasPermission(user, p)) {
                    permissions.add(p);
                }
            }
        }
        return permissions;
    }
}
