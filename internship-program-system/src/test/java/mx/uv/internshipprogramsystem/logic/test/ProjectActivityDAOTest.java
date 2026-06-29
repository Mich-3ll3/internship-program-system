package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            new ProjectActivityDTO(1, "Analisis", "Junio", 1, 2, 40, 7);
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        boolean wasCreated = dao.create(activity, connection);

        assertTrue(wasCreated);
        verify(statement).setString(1, "Analisis");
        verify(statement).setInt(5, 40);
        verify(statement).setInt(6, 7);
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
            "horas_planeadas", 40,
            "proyecto_id", 7
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectActivityDTO> activities = dao.findByProjectId(7);

            assertEquals(1, activities.size());
            assertEquals("Analisis", activities.get(0).getName());
            assertEquals(40, activities.get(0).getPlannedHours());
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
    void saveAllWhenSuccessCommitsTransaction() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet());
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<ProjectActivityDTO> activities = List.of(
                new ProjectActivityDTO(1, "Analisis", "Junio", 1, 2, 40, 7),
                new ProjectActivityDTO(2, "Diseno", "Julio", 3, 4, 30, 7)
            );

            boolean result = dao.saveAll(7, activities);

            assertTrue(result);
            verify(connection).setAutoCommit(false);
            verify(connection).commit();
            verify(connection, never()).rollback();
        }
    }

    @Test
    void saveAllWhenExceptionRollsBackTransaction() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet());
        when(statement.executeUpdate()).thenThrow(new SQLException("Database error"));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<ProjectActivityDTO> activities = List.of(
                new ProjectActivityDTO(1, "Analisis", "Junio", 1, 2, 40, 7)
            );

            assertThrows(Exception.class, () -> {
                dao.saveAll(7, activities);
            });

            verify(connection).setAutoCommit(false);
            verify(connection).rollback();
            verify(connection, never()).commit();
        }
    }

    @Test
    void saveAllWhenExistingRowsExistUpdatesAndMarksAsDeleted() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet(
            row("id", 10, "nombre", "Old Activity", "mes", "Julio", "semana_inicio", 1, "semana_fin", 2, "horas_planeadas", 20, "proyecto_id", 7),
            row("id", 11, "nombre", "__DELETED__11", "mes", "__DELETED__", "semana_inicio", 0, "semana_fin", 0, "horas_planeadas", 0, "proyecto_id", 7)
        ));
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<ProjectActivityDTO> activities = List.of(
                new ProjectActivityDTO("New Activity", "Julio", 1, 2, 40, 7)
            );

            boolean result = dao.saveAll(7, activities);

            assertTrue(result);
            verify(connection).setAutoCommit(false);
            verify(connection).commit();
            verify(connection, never()).rollback();
        }
    }
}
