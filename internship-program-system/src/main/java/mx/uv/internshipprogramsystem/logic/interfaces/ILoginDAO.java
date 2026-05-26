package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface ILoginDAO {
    UserDTO login(String email, String plainPassword) throws BusinessException;
}