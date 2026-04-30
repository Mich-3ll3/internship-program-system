package mx.uv.internshipprogramsystem.test;

import mx.uv.internshipprogramsystem.logic.dao.DocumentDAO;
import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DocumentDAOTest {

    @Test
    public void testInsertDocument() throws BusinessException {
        DocumentDAO dao = new DocumentDAO();
        DocumentDTO doc = new DocumentDTO(0, "Documento de prueba", "pdf", "/tmp/doc1.pdf");
        boolean result = dao.insert(doc);
        Assertions.assertTrue(result, "El documento debería insertarse correctamente");
    }

    @Test
    public void testFindById() throws BusinessException {
        DocumentDAO dao = new DocumentDAO();
        DocumentDTO doc = dao.findById(1);
        Assertions.assertNotNull(doc, "El documento con id=1 debería existir");
    }

    @Test
    public void testDeleteDocument() throws BusinessException {
        DocumentDAO dao = new DocumentDAO();
        boolean result = dao.delete(1);
        Assertions.assertTrue(result, "El documento con id=1 debería eliminarse correctamente");
    }
}
