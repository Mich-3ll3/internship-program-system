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
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ProjectResponsibleDAOTest {
    @Test
    void insertWhenResponsibleIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasInserted = dao.insert(buildResponsible());


            assertTrue(wasInserted);
            verify(statement).setString(1, "Carlos");
            verify(statement).setInt(6, 5);
        }
    }

    @Test
    void findByIdWhenExistsReturnsResponsible() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(responsibleRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ProjectResponsibleDTO> responsible = dao.findById(3);


            assertTrue(responsible.isPresent());
            assertEquals("Carlos", responsible.get().getFirstName());
            assertEquals("Organizacion UV", responsible.get().getOrganizationName());
        }
    }

    @Test
    void findBySearchTextWhenRowsExistReturnsResponsibles() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(responsibleRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectResponsibleDTO> responsibles = dao.findBySearchText("Carlos");


            assertEquals(1, responsibles.size());
            assertEquals("Responsable", responsibles.get(0).getPosition());
            verify(statement).setString(1, "%Carlos%");
            verify(statement).setString(7, "%Carlos%");
        }
    }

    @Test
    void deleteWhenResponsibleExistsReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasDeleted = dao.delete(3);


            assertTrue(wasDeleted);
            verify(statement).setInt(1, 3);
        }
    }

    @Test
    void deleteWhenIdIsInvalidThrowsBusinessException() {

        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();


        assertThrows(BusinessException.class, () -> dao.delete(0));
    }

    @Test
    void insertWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasInserted = dao.insert(buildResponsible());


            assertFalse(wasInserted);
        }
    }

    private ProjectResponsibleDTO buildResponsible() {
        return new ProjectResponsibleDTO(
            3,
            "Carlos",
            "Sanchez",
            "Ramos",
            "carlos@organizacion.mx",
            "Responsable",
            5,
            "Organizacion UV",
            "Sistema de Practicas"
        );
    }

    private java.util.Map<String, Object> responsibleRow() {
        return row(
            "id", 3,
            "nombre", "Carlos",
            "apellido_paterno", "Sanchez",
            "apellido_materno", "Ramos",
            "correo", "carlos@organizacion.mx",
            "cargo", "Responsable",
            "organizacion_id", 5,
            "nombre_organizacion", "Organizacion UV",
            "nombre_proyecto", "Sistema de Practicas"
        );
    }
}
