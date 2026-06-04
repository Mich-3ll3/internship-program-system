package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.ReportDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ReportDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ReportDAOTest {
    @Test
    void registerReportWhenReportIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasRegistered = dao.registerReport(buildReport());

            
            assertTrue(wasRegistered);
            verify(statement).setInt(1, 1);
            verify(statement).setString(5, "PENDIENTE");
            verify(statement).setInt(9, 7);
        }
    }

    @Test
    void getAllReportsWhenRowsExistReturnsReports() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(reportRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ReportDTO> reports = dao.getAllReports();


            assertEquals(1, reports.size());
            assertEquals(LocalDate.of(2026, 6, 5), reports.get(0).getReviewDate());
        }
    }

    @Test
    void updateReportFilePathWhenRowIsUpdatedReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.updateReportFilePath(4, "/reportes/r1.pdf");


            assertTrue(wasUpdated);
            verify(statement).setString(1, "/reportes/r1.pdf");
            verify(statement).setInt(2, 4);
        }
    }

    @Test
    void evaluateReportWhenDataIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasEvaluated = dao.evaluateReport(4, "APROBADO", "Correcto");


            assertTrue(wasEvaluated);
            verify(statement).setString(1, "APROBADO");
            verify(statement).setString(2, "Correcto");
            verify(statement).setInt(3, 4);
        }
    }

    @Test
    void getReportByStudentWhenExistsReturnsReport() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(reportRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ReportDTO> report = dao.getReportByStudent(12);


            assertTrue(report.isPresent());
            assertEquals(4, report.get().getId());
            assertEquals("PENDIENTE", report.get().getStatus());
        }
    }

    @Test
    void getAllProjectsWhenRowsExistReturnsProjectNames() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ReportDAO dao = new ReportDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("nombre", "Sistema")));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<String> projects = dao.getAllProjects();


            assertEquals(List.of("Sistema"), projects);
        }
    }

    @Test
    void evaluateReportWhenStatusIsEmptyThrowsBusinessException() {

        ReportDAO dao = new ReportDAO();


        assertThrows(BusinessException.class, () -> dao.evaluateReport(4, "", "Sin estado"));
    }

    private ReportDTO buildReport() {
        return new ReportDTO(
            1,
            LocalDate.of(2026, 6, 4),
            "Observaciones",
            "MENSUAL",
            "PENDIENTE",
            "/reportes/r1.pdf",
            12,
            30,
            7
        );
    }

    private java.util.Map<String, Object> reportRow() {
        return row(
            "id", 4,
            "numero", 1,
            "fecha", java.sql.Date.valueOf("2026-06-04"),
            "observaciones_generales", "Observaciones",
            "tipo", "MENSUAL",
            "estado", "PENDIENTE",
            "ruta_archivo", "/reportes/r1.pdf",
            "estudiante_id", 12,
            "profesor_id", 30,
            "proyecto_id", 7,
            "fecha_revision", Timestamp.valueOf("2026-06-05 12:00:00")
        );
    }
}
