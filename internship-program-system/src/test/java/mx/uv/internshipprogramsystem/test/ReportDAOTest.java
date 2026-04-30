package mx.uv.internshipprogramsystem.test;

import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;
import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ReportDAOTest {

    @Test
    public void testRegisterReport() throws BusinessException {
        ReportDAO dao = new ReportDAO();
        
        ReportDTO report = new ReportDTO(0, 2, LocalDate.now(), "Observaciones de prueba", "final", "pendiente", "/tmp/report2.pdf", 1, 2, 1);

        boolean result = dao.registerReport(report);

        Assertions.assertTrue(result, "El reporte debería registrarse correctamente");
    }

    @Test
    public void testEvaluateReport() throws BusinessException {
        ReportDAO dao = new ReportDAO();

        boolean result = dao.evaluateReport(1, "revisado", "Todo correcto");

        Assertions.assertTrue(result, "El reporte debería evaluarse correctamente");
    }

    @Test
    public void testGetReportByStudent() throws BusinessException {
        ReportDAO dao = new ReportDAO();

        ReportDTO report = dao.getReportByStudent(1);

        Assertions.assertNotNull(report, "El reporte del estudiante debería existir");
    }
}
