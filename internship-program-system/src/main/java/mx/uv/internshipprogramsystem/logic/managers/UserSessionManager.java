package mx.uv.internshipprogramsystem.logic.managers;

import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;

public final class UserSessionManager {
    
    private static UserDTO currentUser;

    private UserSessionManager() {

    }

    public static void setCurrentUser(UserDTO user) {

        currentUser = user; 
    }

    public static Optional<UserDTO> getCurrentUser() {

        return Optional.ofNullable(currentUser); 
    }

    public static Optional<InternDTO> getCurrentIntern() {
        if (currentUser instanceof InternDTO) {
            return Optional.of((InternDTO) currentUser);
        }
        return Optional.empty(); 
    }

    public static void clearSession() {
        currentUser = null;
    }
}