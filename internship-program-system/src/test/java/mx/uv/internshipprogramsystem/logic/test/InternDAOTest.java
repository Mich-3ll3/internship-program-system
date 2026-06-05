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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class InternDAOTest {
    @Test
    void createWithConnectionWhenInternIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDTO intern = buildIntern();
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);


        boolean wasCreated = dao.create(intern, connection);


        assertTrue(wasCreated);
        verify(statement).setString(1, "zS12345678");
        verify(statement).setInt(2, 25);
    }

    @Test
    void updateWhenInternIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildIntern());


            assertTrue(wasUpdated);
            verify(statement).setInt(1, 25);
            verify(statement).setString(2, "zS12345678");
        }
    }

    @Test
    void findByEnrollmentNumberWhenExistsReturnsIntern() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(internRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<InternDTO> intern = dao.findByEnrollmentNumber("zS12345678");


            assertTrue(intern.isPresent());
            assertEquals("zS12345678", intern.get().getEnrollmentNumber());
            assertEquals("12345", intern.get().getNrc());
        }
    }

    @Test
    void findAllWhenRowsExistReturnsInterns() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(internRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<InternDTO> interns = dao.findAll();


            assertEquals(1, interns.size());
            assertEquals("Ana", interns.get(0).getName());
        }
    }

    @Test
    void countAllReturnsTotal() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
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
        InternDAO dao = new InternDAO();


        assertThrows(BusinessException.class, () -> dao.create(intern, mock(Connection.class)));
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
        intern.setEnrollmentNumber("zS12345678");
        intern.setId(25);

        return intern;
    }

    private java.util.Map<String, Object> internRow() {
        return row(
            "matricula", "zS12345678",
            "NRC", "12345",
            "id", 25,
            "correo_institucional", "ana@estudiantes.uv.mx",
            "nombre", "Ana",
            "apellido_paterno", "Lopez",
            "apellido_materno", "Diaz",
            "activo", true
        );
    }
}
