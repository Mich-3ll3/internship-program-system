package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IUserDAO {
    int create(UserDTO user, Connection connection) throws BusinessException, DataAccessException;
    int create(UserDTO user) throws BusinessException, DataAccessException;
    boolean update(UserDTO user) throws BusinessException, DataAccessException;
    Optional<UserDTO> findByInstitutionalEmail(String institutionalEmail) throws BusinessException, DataAccessException;
    boolean changeStatus(int userId, boolean isActive) throws BusinessException, DataAccessException;
    int countActiveUsers() throws BusinessException, DataAccessException;
    boolean activateAccount(int userId, String passwordHash) throws BusinessException, DataAccessException;
}