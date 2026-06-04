package mx.uv.internshipprogramsystem.logic.managers;

import mx.uv.internshipprogramsystem.logic.dao.LoginDAO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class LoginManager {
    private final LoginDAO loginDAO;

    public LoginManager() {
        loginDAO = new LoginDAO();
    }

    public UserDTO login(String email, String plainPassword) throws BusinessException {
        UserDTO loggedUser = loginDAO.login(email, plainPassword);
        return loggedUser;
    }
}