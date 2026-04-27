package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.interfaces.IDocumentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentDAO implements IDocumentDAO {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDAO.class);

    @Override
    public boolean insert(DocumentDTO document) throws BusinessException {
        String insertDocumentQuery = "INSERT INTO DOCUMENTO (nombre, tipo, ruta) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertDocumentStatement = connection.prepareStatement(insertDocumentQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertDocumentStatement.setString(1, document.getName());
            insertDocumentStatement.setString(2, document.getType());
            insertDocumentStatement.setString(3, document.getPath());

            int affectedRows = insertDocumentStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insertDocumentStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        document.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Documento '{}' insertado correctamente con id {}", document.getName(), document.getId());
                return true;
            } else {
                logger.warn("No se insertó el documento '{}'", document.getName());
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error insertando documento '{}'", document.getName(), sqlException);
            throw new BusinessException("Error insertando documento " + document.getName(), sqlException);
        }
    }

    @Override
    public DocumentDTO findById(int id) throws BusinessException {
        String selectDocumentById = "SELECT * FROM DOCUMENTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectDocument = connection.prepareStatement(selectDocumentById)) {

            statementSelectDocument.setInt(1, id);
            try (ResultSet resultSetDocument = statementSelectDocument.executeQuery()) {
                if (resultSetDocument.next()) {
                    DocumentDTO documentResult = new DocumentDTO(
                        resultSetDocument.getInt("id"),
                        resultSetDocument.getString("nombre"),
                        resultSetDocument.getString("tipo"),
                        resultSetDocument.getString("ruta")
                    );
                    logger.info("Documento encontrado con id {}", id);
                    return documentResult;
                } else {
                    logger.warn("No se encontró documento con id {}", id);
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            logger.error("Error buscando documento con id {}", id, sqlException);
            throw new BusinessException("Error buscando documento con id " + id, sqlException);
        }
    }

    @Override
    public boolean delete(int id) throws BusinessException {
        String deleteDocumentById = "DELETE FROM DOCUMENTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteDocumentStatement = connection.prepareStatement(deleteDocumentById)) {

            deleteDocumentStatement.setInt(1, id);
            int affectedRows = deleteDocumentStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Documento con id {} eliminado correctamente", id);
                return true;
            } else {
                logger.warn("No se eliminó documento con id {}", id);
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error eliminando documento con id {}", id, sqlException);
            throw new BusinessException("Error eliminando documento con id " + id, sqlException);
        }
    }
}
