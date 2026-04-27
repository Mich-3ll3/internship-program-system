package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.DocumentException;

public interface IDocumentDAO {
    boolean insert(DocumentDTO document) throws DocumentException;
    DocumentDTO findById(int id) throws DocumentException;
    boolean delete(int id) throws DocumentException;
}
