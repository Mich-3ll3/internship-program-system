package mx.uv.internshipprogramsystem.logic.managers;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;

public final class UserSessionManager {

    private static UserDTO currentUser;

    private UserSessionManager() {

    }

    public static void setCurrentUser(
            UserDTO user
    ) {
        currentUser =
            user;
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static Optional<UserDTO> getCurrentUserOptional() {
        return Optional.ofNullable(
            currentUser
        );
    }

    public static Optional<InternDTO> getCurrentIntern() {
        Optional<InternDTO> currentIntern =
            Optional.empty();

        if (currentUser instanceof InternDTO) {
            currentIntern =
                Optional.of(
                    (InternDTO) currentUser
                );
        }

        return currentIntern;
    }

    public static void clearSession() {
        currentUser =
            null;
    }
}