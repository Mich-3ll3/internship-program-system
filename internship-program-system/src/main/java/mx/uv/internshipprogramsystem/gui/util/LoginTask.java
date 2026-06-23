package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.LoginManager;

public class LoginTask extends Task<UserDTO> {
    private final String email;
    private final String password;

    public LoginTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    protected UserDTO call() throws Exception {
        LoginManager loginManager = new LoginManager();
        return loginManager.login(email, password);
    }
}
