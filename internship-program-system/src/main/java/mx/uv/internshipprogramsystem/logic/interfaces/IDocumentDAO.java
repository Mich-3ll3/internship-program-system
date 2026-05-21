package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IDocumentDAO {

    boolean insert(DocumentDTO document) throws BusinessException;

    Optional<DocumentDTO> findById(int id) throws BusinessException;

    boolean delete(int id) throws BusinessException;
}
