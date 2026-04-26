package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IUserDAO {
    boolean create(UserDTO user, String plainPassword) throws BusinessException;
    boolean update(UserDTO user) throws BusinessException;
    boolean changeStatus(String email, boolean isActive) throws BusinessException;
    boolean login(String email, String plainPassword) throws BusinessException;
}
