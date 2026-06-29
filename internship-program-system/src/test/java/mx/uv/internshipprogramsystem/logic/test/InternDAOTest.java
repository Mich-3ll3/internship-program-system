package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class InternDAOTest {
    private Connection connection;
    private PreparedStatement statement;
    private InternDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        dao = new InternDAO();
        mockPreparedStatement(connection, statement);
    }

    @Test
    void createWithConnectionWhenInternIsValidReturnsTrue()
            throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        boolean wasCreated = dao.create(buildIntern(), connection);

        assertTrue(wasCreated);
    }

    @Test
    void updateWhenInternIsValidReturnsTrue() throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasUpdated = dao.update(buildIntern());

            assertTrue(wasUpdated);
        }
    }

    @Test
    void findByEnrollmentNumberWhenExistsReturnsEnrollment()
            throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet(internRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            Optional<InternDTO> intern =
                dao.findByEnrollmentNumber("S12345678");

            assertEquals("S12345678", intern.orElseThrow().getEnrollmentNumber());
        }
    }

    @Test
    void findAllWhenRowsExistReturnsInternName() throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet(internRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<InternDTO> interns = dao.findAll();

            assertEquals("Ana", interns.get(0).getName());
        }
    }

    @Test
    void countAllReturnsTotal() throws Exception {
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 4)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            int total = dao.countAll();

            assertEquals(4, total);
        }
    }

    @Test
    void createWhenEnrollmentNumberIsInvalidThrowsBusinessException() {
        InternDTO intern = buildIntern();
        intern.setEnrollmentNumber("123");

        assertThrows(
            BusinessException.class,
            () -> dao.create(intern, connection)
        );
    }

    @Test
    void updateWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildIntern());


            assertFalse(wasUpdated);
        }
    }

    private InternDTO buildIntern() {
        InternDTO intern = new InternDTO();
        intern.setEnrollmentNumber("S12345678");
        intern.setId(25);

        return intern;
    }

    private java.util.Map<String, Object> internRow() {
        return row(
            "matricula", "S12345678",
            "NRC", "12345",
            "estado_experiencia", "ACTIVA",
            "historial_nrc", "12345 - 202651 - ACTIVA",
            "id", 25,
            "correo_institucional", "ana@estudiantes.uv.mx",
            "nombre", "Ana",
            "apellido_paterno", "Lopez",
            "apellido_materno", "Diaz",
            "activo", true
        );
    }
}
