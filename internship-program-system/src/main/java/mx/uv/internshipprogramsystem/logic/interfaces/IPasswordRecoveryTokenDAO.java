package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IPasswordRecoveryTokenDAO {

    boolean create(
        PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException;

    Optional<PasswordRecoveryTokenDTO> findByTokenHash(
        String tokenHash
    ) throws BusinessException;

    boolean markAsUsed(
        int tokenId
    ) throws BusinessException;

    boolean invalidateTokensByUserId(
        int userId
    ) throws BusinessException;
}