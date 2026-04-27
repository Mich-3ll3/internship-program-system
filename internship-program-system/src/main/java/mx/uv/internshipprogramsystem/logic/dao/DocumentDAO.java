package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.DocumentException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import mx.uv.internshipprogramsystem.logic.interfaces.IDocumentDAO;

public class DocumentDAO implements IDocumentDAO {
    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

    @Override
    public boolean insert(DocumentDTO document) throws DocumentException {
        boolean operationSuccessful = false;
        String insertDocumentQuery = "INSERT INTO DOCUMENTO (nombre, tipo, ruta) VALUES (?, ?, ?)";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement insertDocumentStatement = Connection.prepareStatement(insertDocumentQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new DocumentException("Error inserting document", exception);
        }
        return operationSuccessful;
    }

    @Override
    public DocumentDTO findById(int id) throws DocumentException {
        DocumentDTO documentResult = null;
        String selectDocumentById = "SELECT * FROM DOCUMENTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectDocument = Connection.prepareStatement(selectDocumentById)) {

            statementSelectDocument.setInt(1, id);
            try (ResultSet resultSetDocument = statementSelectDocument.executeQuery()) {
                if (resultSetDocument.next()) {
                    documentResult = new DocumentDTO(
                        resultSetDocument.getInt("id"),
                        resultSetDocument.getString("nombre"),
                        resultSetDocument.getString("tipo"),
                        resultSetDocument.getString("ruta")
                    );
                }
            }
        } catch (SQLException exception) {
            throw new DocumentException("Error finding document with id " + id, exception);
        }
        return documentResult;
    }

    @Override
    public boolean delete(int id) throws DocumentException {
        boolean operationSuccessful = false;
        String deleteDocumentById = "DELETE FROM DOCUMENTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement deleteDocumentStatement = Connection.prepareStatement(deleteDocumentById)) {

            deleteDocumentStatement.setInt(1, id);
            int affectedRows = deleteDocumentStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new DocumentException("Error deleting document with id " + id, exception);
        }
        return operationSuccessful;
    }
}
