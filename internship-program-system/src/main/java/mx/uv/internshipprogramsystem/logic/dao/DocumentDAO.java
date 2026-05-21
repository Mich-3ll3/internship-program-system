package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IDocumentDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class DocumentDAO implements IDocumentDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDAO.class);

    @Override
    public boolean insert(DocumentDTO document) throws BusinessException {
        InputValidator.validateNotNull(document, "DocumentDTO no puede ser nulo.");
        String insertDocumentQuery =
            "INSERT INTO DOCUMENTO (nombre, tipo, ruta) VALUES (?, ?, ?)";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertDocumentStatement = connection.prepareStatement(
                 insertDocumentQuery,
                 Statement.RETURN_GENERATED_KEYS
             )) {
            insertDocumentStatement.setString(1, document.getName());
            insertDocumentStatement.setString(2, document.getType());
            insertDocumentStatement.setString(3, document.getPath());

            int affectedRows = insertDocumentStatement.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.warn("No se insertó el documento '{}'", document.getName());
                return false;
            }

            assignGeneratedId(document, insertDocumentStatement);
            LOGGER.info(
                "Documento '{}' insertado correctamente con id {}",
                document.getName(),
                document.getId()
            );
            return true;
        } catch (SQLException sqlException) {
            LOGGER.error("Error insertando documento '{}'", document.getName(), sqlException);
            throw new BusinessException(
                "Error insertando documento " + document.getName(),
                sqlException
            );
        }
    }

    @Override
    public Optional<DocumentDTO> findById(int id) throws BusinessException {
        InputValidator.validatePositive(id, "El id del documento debe ser positivo.");
        String selectDocumentById = "SELECT id, nombre, tipo, ruta FROM DOCUMENTO WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectDocument =
                 connection.prepareStatement(selectDocumentById)) {
            statementSelectDocument.setInt(1, id);

            try (ResultSet resultSetDocument = statementSelectDocument.executeQuery()) {
                if (!resultSetDocument.next()) {
                    LOGGER.warn("No se encontró documento con id {}", id);
                    return Optional.empty();
                }

                LOGGER.info("Documento encontrado con id {}", id);
                return Optional.of(buildDocument(resultSetDocument));
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error buscando documento con id {}", id, sqlException);
            throw new BusinessException(
                "Error buscando documento con id " + id,
                sqlException
            );
        }
    }

    @Override
    public boolean delete(int id) throws BusinessException {
        InputValidator.validatePositive(id, "El id del documento debe ser positivo.");
        String deleteDocumentById = "DELETE FROM DOCUMENTO WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteDocumentStatement =
                 connection.prepareStatement(deleteDocumentById)) {
            deleteDocumentStatement.setInt(1, id);
            int affectedRows = deleteDocumentStatement.executeUpdate();

            if (affectedRows > 0) {
                LOGGER.info("Documento con id {} eliminado correctamente", id);
                return true;
            }

            LOGGER.warn("No se eliminó documento con id {}", id);
            return false;
        } catch (SQLException sqlException) {
            LOGGER.error("Error eliminando documento con id {}", id, sqlException);
            throw new BusinessException(
                "Error eliminando documento con id " + id,
                sqlException
            );
        }
    }

    private void assignGeneratedId(
            DocumentDTO document,
            PreparedStatement insertDocumentStatement
    ) throws SQLException {
        try (ResultSet generatedKeys = insertDocumentStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                document.setId(generatedKeys.getInt(1));
            }
        }
    }

    private DocumentDTO buildDocument(ResultSet resultSetDocument) throws SQLException {
        return new DocumentDTO(
            resultSetDocument.getInt("id"),
            resultSetDocument.getString("nombre"),
            resultSetDocument.getString("tipo"),
            resultSetDocument.getString("ruta")
        );
    }
}
