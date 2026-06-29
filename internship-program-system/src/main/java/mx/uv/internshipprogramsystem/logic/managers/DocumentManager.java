package mx.uv.internshipprogramsystem.logic.managers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.DocumentDAO;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class DocumentManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentManager.class);
    private static final int UNASSIGNED_PROFESSOR_ID = -1;
    
    private final DocumentDAO documentDAO;

    public DocumentManager() {
        this.documentDAO = new DocumentDAO();
    }

    public List<DocumentDTO> getDocumentsByInternId(int internId) throws BusinessException {
        List<DocumentDTO> documentList = new ArrayList<>();
        
        try {
            documentList = documentDAO.getDocumentsByInternId(internId);
        } catch (BusinessException daoException) {
            LOGGER.error("Business rule failed while fetching documents: {}", daoException.getMessage());
            throw daoException;
        } finally {
            LOGGER.debug("Fetch documents business process finished.");
        }
        
        return documentList;
    }
    
    public boolean registerDocument(DocumentDTO documentToRegister, int internId, int professorId) throws BusinessException {
        boolean isRegistered = false;
        
        try {
            isRegistered = documentDAO.insertDocumentForIntern(documentToRegister, internId, professorId);
        } catch (BusinessException daoException) {
            LOGGER.error("Business rule failed while registering document: {}", daoException.getMessage());
            throw daoException;
        } finally {
            LOGGER.debug("Register document business process finished.");
        }
        
        return isRegistered;
    }
    
    public int getAssignedProfessorId(int internId) throws BusinessException {
        int professorId = UNASSIGNED_PROFESSOR_ID;
        
        try {
            professorId = documentDAO.getAssignedProfessorId(internId);
        } catch (BusinessException daoException) {
            LOGGER.error("Business rule failed while fetching assigned professor: {}", daoException.getMessage());
            throw daoException;
        } finally {
            LOGGER.debug("Fetch assigned professor business process finished.");
        }
        
        return professorId;
    }
}