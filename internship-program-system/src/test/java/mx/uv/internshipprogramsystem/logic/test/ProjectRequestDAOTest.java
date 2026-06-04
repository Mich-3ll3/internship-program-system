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
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.ProjectRequestDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ProjectRequestDAOTest {
    @Test
    void insertWhenRequestIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectRequestDAO dao = new ProjectRequestDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasInserted = dao.insert(buildRequest());


            assertTrue(wasInserted);
            verify(statement).setInt(1, 12);
            verify(statement).setInt(2, 7);
            verify(statement).setInt(3, 1);
        }
    }

    @Test
    void findByStudentWhenRowsExistReturnsRequests() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectRequestDAO dao = new ProjectRequestDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "estudiante_id", 12,
            "proyecto_id", 7,
            "prioridad", 1
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectRequestDTO> requests = dao.findByStudent(12);


            assertEquals(1, requests.size());
            assertEquals(7, requests.get(0).getProjectId());
        }
    }

    @Test
    void deleteWhenRequestExistsReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectRequestDAO dao = new ProjectRequestDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasDeleted = dao.delete(buildRequest());


            assertTrue(wasDeleted);
            verify(statement).setInt(1, 12);
            verify(statement).setInt(2, 7);
        }
    }

    @Test
    void findByStudentWhenIdIsInvalidThrowsBusinessException() {

        ProjectRequestDAO dao = new ProjectRequestDAO();


        assertThrows(BusinessException.class, () -> dao.findByStudent(0));
    }

    private ProjectRequestDTO buildRequest() {
        return new ProjectRequestDTO(12, 7, 1);
    }
}
