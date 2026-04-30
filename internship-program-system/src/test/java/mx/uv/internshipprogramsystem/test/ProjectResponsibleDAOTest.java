package mx.uv.internshipprogramsystem.test;

import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProjectResponsibleDAOTest {

    @Test
    public void testInsertResponsible() throws BusinessException {
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        ProjectResponsibleDTO responsible = new ProjectResponsibleDTO(0, "Juan", "Pérez", "López", "juan.perez@test.com", "Coordinador", 1);
        boolean result = dao.insert(responsible);
        Assertions.assertTrue(result, "El responsable debería insertarse correctamente");
    }

    @Test
    public void testFindById() throws BusinessException {
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        ProjectResponsibleDTO responsible = dao.findById(1);
        Assertions.assertNotNull(responsible, "El responsable con id=1 debería existir");
    }

    @Test
    public void testDeleteResponsible() throws BusinessException {
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        boolean result = dao.delete(1);
        Assertions.assertTrue(result, "El responsable con id=1 debería eliminarse correctamente");
    }
}
