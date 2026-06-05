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
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ProfessorDAOTest {
    @Test
    void createWithConnectionWhenProfessorIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDTO professor = buildProfessor();
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);


        boolean wasCreated = dao.create(professor, connection);


        assertTrue(wasCreated);
        verify(statement).setString(1, "123456");
        verify(statement).setBoolean(2, true);
        verify(statement).setInt(3, 30);
    }

    @Test
    void updateWhenProfessorIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildProfessor());


            assertTrue(wasUpdated);
            verify(statement).setInt(3, 30);
        }
    }

    @Test
    void findByStaffNumberWhenExistsReturnsProfessor() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(professorRow("grupos", 2)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ProfessorDTO> professor = dao.findByStaffNumber("123456");


            assertTrue(professor.isPresent());
            assertEquals("123456", professor.get().getStaffNumber());
            assertEquals(2, professor.get().getGroups());
        }
    }

    @Test
    void findAllWhenRowsExistReturnsProfessors() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(professorRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProfessorDTO> professors = dao.findAll();


            assertEquals(1, professors.size());
            assertEquals("Maria", professors.get(0).getName());
        }
    }

    @Test
    void findCoordinatorWhenExistsReturnsCoordinator() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(professorRow("rol", "PROFESOR")));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ProfessorDTO> coordinator = dao.findCoordinator();


            assertTrue(coordinator.isPresent());
            assertEquals(UserRole.PROFESSOR, coordinator.get().getRole());
        }
    }

    @Test
    void existsCoordinatorWhenCountIsPositiveReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 1)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean exists = dao.existsCoordinator();


            assertTrue(exists);
        }
    }

    @Test
    void createWhenStaffNumberIsInvalidThrowsBusinessException() {

        ProfessorDTO professor = buildProfessor();
        professor.setStaffNumber("ABC");
        ProfessorDAO dao = new ProfessorDAO();


        assertThrows(BusinessException.class, () -> dao.create(professor, mock(Connection.class)));
    }

    @Test
    void updateWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildProfessor());


            assertFalse(wasUpdated);
        }
    }

    private ProfessorDTO buildProfessor() {
        ProfessorDTO professor = new ProfessorDTO();
        professor.setId(30);
        professor.setStaffNumber("123456");
        professor.setIsCoordinator(true);

        return professor;
    }

    private java.util.Map<String, Object> professorRow(Object... extra) {
        java.util.Map<String, Object> row = row(
            "numero_personal", "123456",
            "es_coordinador", true,
            "id", 30,
            "correo_institucional", "maria@uv.mx",
            "nombre", "Maria",
            "apellido_paterno", "Perez",
            "apellido_materno", "Garcia",
            "activo", true
        );

        for (int index = 0; index < extra.length; index += 2) {
            row.put((String) extra[index], extra[index + 1]);
        }

        return row;
    }
}
