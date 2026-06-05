package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;

class ProjectActivityDAOTest {
    @Test
    void createWithConnectionWhenActivityIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        ProjectActivityDTO activity =
            new ProjectActivityDTO(1, "Analisis", "Junio", 1, 2, 7);
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);


        boolean wasCreated = dao.create(activity, connection);


        assertTrue(wasCreated);
        verify(statement).setString(1, "Analisis");
        verify(statement).setInt(5, 7);
    }

    @Test
    void findByProjectIdWhenRowsExistReturnsActivities() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "id", 1,
            "nombre", "Analisis",
            "mes", "Junio",
            "semana_inicio", 1,
            "semana_fin", 2,
            "proyecto_id", 7
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectActivityDTO> activities = dao.findByProjectId(7);


            assertEquals(1, activities.size());
            assertEquals("Analisis", activities.get(0).getName());
        }
    }

    @Test
    void deleteByProjectIdWithConnectionReturnsTrueEvenWhenZeroRows()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);


        boolean wasDeleted = dao.deleteByProjectId(7, connection);


        assertTrue(wasDeleted);
        verify(statement).setInt(1, 7);
    }

    @Test
    void createWithConnectionWhenNoRowsAreAffectedReturnsFalse()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        ProjectActivityDTO activity =
            new ProjectActivityDTO(1, "Analisis", "Junio", 1, 2, 7);
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        boolean wasCreated = dao.create(activity, connection);

        assertFalse(wasCreated);
    }
}
