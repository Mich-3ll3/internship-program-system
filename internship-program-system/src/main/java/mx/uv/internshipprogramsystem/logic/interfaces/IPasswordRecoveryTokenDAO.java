package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IPasswordRecoveryTokenDAO {

    boolean create(
        PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException, DataAccessException;

    Optional<PasswordRecoveryTokenDTO> findByTokenHash(
        String tokenHash
    ) throws BusinessException, DataAccessException;

    boolean markAsUsed(
        int tokenId
    ) throws BusinessException, DataAccessException;

    boolean invalidateTokensByUserId(
        int userId
    ) throws BusinessException, DataAccessException;
}