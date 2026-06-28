package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    private static final int MINIMUM_AFFECTED_ROWS = 0;
    private static final int INITIAL_PARAMETER_INDEX = 1;
    private static final int UNASSIGNED_PROFESSOR_ID = -1;
    
    private static final String DEFAULT_PENDING_STATUS = "pendiente";
    private static final String DEFAULT_NOT_AVAILABLE_DATE = "N/A";

    @Override
    public boolean insert(DocumentDTO document) throws BusinessException {
        boolean isInserted = false;
        InputValidator.validateNotNull(document, "DocumentDTO no puede ser nulo.");
        String insertQuery = "INSERT INTO DOCUMENTO (nombre, tipo, ruta) VALUES (?, ?, ?)";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
             
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setString(parameterIndex++, document.getName());
            preparedStatement.setString(parameterIndex++, document.getType());
            preparedStatement.setString(parameterIndex, document.getPath());

            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > MINIMUM_AFFECTED_ROWS) {
                assignGeneratedId(document, preparedStatement);
                isInserted = true;
                LOGGER.info("Documento '{}' insertado correctamente.", document.getName());
            } else {
                LOGGER.warn("No se insertó el documento '{}'", document.getName());
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error insertando documento '{}'", document.getName(), sqlException);
            throw new BusinessException("Error insertando documento " + document.getName(), sqlException);
        } finally {
            LOGGER.debug("Basic insert document process finished.");
        }
        
        return isInserted;
    }

    public boolean insertDocumentForIntern(DocumentDTO document, int internId, int professorId) throws BusinessException {
        boolean isInserted = false;
        InputValidator.validateNotNull(document, "DocumentDTO no puede ser nulo.");
        
        String insertQuery = "INSERT INTO DOCUMENTO (nombre, tipo, ruta, estado, estudiante_id, profesor_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setString(parameterIndex++, document.getName());
            preparedStatement.setString(parameterIndex++, document.getType());
            preparedStatement.setString(parameterIndex++, document.getPath());
            preparedStatement.setString(parameterIndex++, DEFAULT_PENDING_STATUS);
            preparedStatement.setInt(parameterIndex++, internId);
            preparedStatement.setInt(parameterIndex, professorId);
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > MINIMUM_AFFECTED_ROWS) {
                assignGeneratedId(document, preparedStatement);
                isInserted = true;
                LOGGER.info("Documento de estudiante '{}' insertado correctamente.", document.getName());
            } else {
                LOGGER.warn("No se insertó el documento del estudiante '{}'", document.getName());
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error insertando documento de estudiante '{}'", document.getName(), sqlException);
            throw new BusinessException("Error insertando documento del estudiante " + document.getName(), sqlException);
        } finally {
            LOGGER.debug("Insert document for intern process finished.");
        }
        
        return isInserted;
    }

    public List<DocumentDTO> getDocumentsByInternId(int internId) throws BusinessException {
        List<DocumentDTO> documentList = new ArrayList<>();
        String query = "SELECT id, nombre, tipo, ruta, fecha_subida, estado FROM DOCUMENTO WHERE estudiante_id = ?";
        
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setInt(parameterIndex, internId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    DocumentDTO document = mapResultSetToDocument(resultSet);
                    documentList.add(document);
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Database error while fetching documents for intern {}: {}", internId, sqlException.getMessage());
            throw new BusinessException("Error de conexión al consultar los documentos.", sqlException);
        } finally {
            LOGGER.debug("Fetch documents process finished for intern ID: {}", internId);
        }
        
        return documentList;
    }

    public int getAssignedProfessorId(int internId) throws BusinessException {
        int professorId = UNASSIGNED_PROFESSOR_ID; 
        String query = "SELECT profesor_id FROM ASIGNACION_PROYECTO WHERE estudiante_id = ?";
        
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setInt(parameterIndex, internId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    professorId = resultSet.getInt("profesor_id");
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Database error while fetching assigned professor for intern {}: {}", internId, sqlException.getMessage());
            throw new BusinessException("Error de conexión al consultar el profesor asignado.", sqlException);
        } finally {
            LOGGER.debug("Fetch assigned professor process finished for intern ID: {}", internId);
        }
        
        return professorId;
    }

    @Override
    public Optional<DocumentDTO> findById(int id) throws BusinessException {
        Optional<DocumentDTO> documentOptional = Optional.empty();
        InputValidator.validatePositive(id, "El id del documento debe ser positivo.");
        String selectQuery = "SELECT id, nombre, tipo, ruta FROM DOCUMENTO WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
             
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setInt(parameterIndex, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    DocumentDTO document = new DocumentDTO(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("tipo"),
                        resultSet.getString("ruta")
                    );
                    documentOptional = Optional.of(document);
                    LOGGER.info("Documento encontrado con id {}", id);
                } else {
                    LOGGER.warn("No se encontró documento con id {}", id);
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error buscando documento con id {}", id, sqlException);
            throw new BusinessException("Error buscando documento con id " + id, sqlException);
        } finally {
            LOGGER.debug("Find document by ID process finished.");
        }
        
        return documentOptional;
    }

    @Override
    public boolean delete(int id) throws BusinessException {
        boolean isDeleted = false;
        InputValidator.validatePositive(id, "El id del documento debe ser positivo.");
        String deleteQuery = "DELETE FROM DOCUMENTO WHERE id = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
             
            int parameterIndex = INITIAL_PARAMETER_INDEX;
            preparedStatement.setInt(parameterIndex, id);
            
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > MINIMUM_AFFECTED_ROWS) {
                isDeleted = true;
                LOGGER.info("Documento con id {} eliminado correctamente", id);
            } else {
                LOGGER.warn("No se eliminó documento con id {}", id);
            }
        } catch (SQLException sqlException) {
            LOGGER.error("Error eliminando documento con id {}", id, sqlException);
            throw new BusinessException("Error eliminando documento con id " + id, sqlException);
        } finally {
            LOGGER.debug("Delete document process finished.");
        }
        
        return isDeleted;
    }

    private void assignGeneratedId(DocumentDTO document, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int firstColumnIndex = INITIAL_PARAMETER_INDEX;
                document.setId(generatedKeys.getInt(firstColumnIndex));
            }
        }
    }

    private DocumentDTO mapResultSetToDocument(ResultSet resultSet) throws SQLException {
        DocumentDTO document = new DocumentDTO();
        document.setId(resultSet.getInt("id"));
        document.setName(resultSet.getString("nombre"));
        document.setType(resultSet.getString("tipo"));
        document.setPath(resultSet.getString("ruta"));
        
        java.sql.Timestamp dbDate = resultSet.getTimestamp("fecha_subida");
        if (dbDate != null) {
            document.setUploadDate(dbDate.toString());
        } else {
            document.setUploadDate(DEFAULT_NOT_AVAILABLE_DATE);
        }
        
        String dbStatus = resultSet.getString("estado");
        if (dbStatus != null) {
             document.setStatus(dbStatus);
        } else {
             document.setStatus(DEFAULT_PENDING_STATUS);
        }
        
        return document;
    }
}