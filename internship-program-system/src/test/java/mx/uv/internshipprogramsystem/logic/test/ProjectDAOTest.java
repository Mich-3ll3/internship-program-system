package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.generatedKeys;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatementWithGeneratedKeys;
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
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ProjectDAOTest {
    @Test
    void createWhenProjectIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(buildProject());


            assertTrue(wasCreated);
            verify(statement).setString(1, "Sistema de Practicas");
            verify(statement).setInt(9, 480);
            verify(statement).setBoolean(12, true);
        }
    }

    @Test
    void findAllWhenRowsExistReturnsProjects() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(projectRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectDTO> projects = dao.findAll();


            assertEquals(1, projects.size());
            assertEquals("Sistema de Practicas", projects.get(0).getName());
        }
    }

    @Test
    void findByIdWhenExistsReturnsProject() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(projectRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ProjectDTO> project = dao.findById(7);


            assertTrue(project.isPresent());
            assertEquals(7, project.get().getId());
        }
    }

    @Test
    void countAllReturnsTotal() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 6)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            int total = dao.countAll();


            assertEquals(6, total);
        }
    }

    @Test
    void updateWithConnectionWhenProjectIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);


        boolean wasUpdated = dao.update(buildProject(), connection);


        assertTrue(wasUpdated);
        verify(statement).setInt(13, 7);
    }

    @Test
    void deactivateWhenIdIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasDeactivated = dao.deactivate(7);


            assertTrue(wasDeactivated);
            verify(statement).setInt(1, 7);
        }
    }

    @Test
    void createAndReturnIdWhenGeneratedKeyExistsReturnsId() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatementWithGeneratedKeys(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);
        when(statement.getGeneratedKeys()).thenReturn(generatedKeys(99));

        int generatedId = dao.createAndReturnId(buildProject(), connection);


        assertEquals(99, generatedId);
    }

    @Test
    void findByIdWhenIdIsInvalidThrowsBusinessException() {

        ProjectDAO dao = new ProjectDAO();


        assertThrows(BusinessException.class, () -> dao.findById(0));
    }

    @Test
    void deactivateWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasDeactivated = dao.deactivate(7);


            assertFalse(wasDeactivated);
        }
    }

    @Test
    void createWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(buildProject());


            assertFalse(wasCreated);
        }
    }

    @Test
    void findByIdWhenNoRowsReturnsEmpty() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ProjectDTO> project = dao.findById(7);


            assertTrue(project.isEmpty());
        }
    }

    private ProjectDTO buildProject() {
        return new ProjectDTO(
            7,
            "Sistema de Practicas",
            "Descripcion general",
            "Objetivo general",
            "Objetivos inmediatos",
            "Objetivos mediatos",
            "Metodologia",
            "Recursos",
            "Responsabilidades",
            480,
            5,
            3,
            true
        );
    }

    private java.util.Map<String, Object> projectRow() {
        return row(
            "id", 7,
            "nombre", "Sistema de Practicas",
            "descripcion_general", "Descripcion general",
            "objetivo_general", "Objetivo general",
            "objetivos_inmediatos", "Objetivos inmediatos",
            "objetivos_mediatos", "Objetivos mediatos",
            "metodologia", "Metodologia",
            "recursos", "Recursos",
            "responsabilidades", "Responsabilidades",
            "duracion", 480,
            "organizacion_id", 5,
            "responsable_id", 3,
            "activo", true
        );
    }
}
