package mx.uv.internshipprogramsystem.logic.managers;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;

public final class UserSessionManager {
    private static UserDTO currentUser;

    private UserSessionManager() {
    }

    public static void setCurrentUser(UserDTO user) {
        currentUser = user;
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }
}