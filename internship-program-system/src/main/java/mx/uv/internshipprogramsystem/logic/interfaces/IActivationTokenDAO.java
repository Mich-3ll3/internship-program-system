package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IActivationTokenDAO {
    boolean create(ActivationTokenDTO activationToken) throws BusinessException;

    Optional<ActivationTokenDTO> findByTokenHash(String tokenHash) throws BusinessException;

    boolean markAsUsed(int tokenId) throws BusinessException;
}