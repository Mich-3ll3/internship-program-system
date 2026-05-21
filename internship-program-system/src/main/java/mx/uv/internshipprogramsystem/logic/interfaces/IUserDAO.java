package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IUserDAO {
    int create(UserDTO user) throws BusinessException;
    boolean update(UserDTO user) throws BusinessException;
    boolean changeStatus(int userId, boolean isActive) throws BusinessException;
    UserDTO login(String email, String plainPassword) throws BusinessException;
    int countActiveUsers() throws BusinessException;
    boolean activateAccount(int userId, String passwordHash) throws BusinessException;
}
