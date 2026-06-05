package mx.uv.internshipprogramsystem.logic.interfaces;

import java.sql.Connection;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IUserDAO {
    int create(UserDTO user, Connection connection) throws BusinessException;
    int create(UserDTO user) throws BusinessException;
    boolean update(UserDTO user) throws BusinessException;
    Optional<UserDTO> findByInstitutionalEmail(String institutionalEmail) throws BusinessException;
    boolean changeStatus(int userId, boolean isActive) throws BusinessException;
    int countActiveUsers() throws BusinessException;
    boolean activateAccount(int userId, String passwordHash) throws BusinessException;
}
