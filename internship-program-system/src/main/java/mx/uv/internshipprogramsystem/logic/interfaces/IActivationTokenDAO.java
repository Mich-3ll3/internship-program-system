package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IActivationTokenDAO {
    boolean create(ActivationTokenDTO activationToken) throws BusinessException, DataAccessException;

    Optional<ActivationTokenDTO> findByTokenHash(String tokenHash) throws BusinessException, DataAccessException;

    boolean markAsUsed(int tokenId) throws BusinessException, DataAccessException;
}