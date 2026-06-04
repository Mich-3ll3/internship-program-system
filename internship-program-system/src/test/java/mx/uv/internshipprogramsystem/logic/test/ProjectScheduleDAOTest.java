package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.ProjectScheduleDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;

class ProjectScheduleDAOTest {
    @Test
    void createWithConnectionWhenScheduleIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectScheduleDAO dao = new ProjectScheduleDAO();
        ProjectScheduleDTO schedule = buildSchedule();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);


        boolean wasCreated = dao.create(schedule, connection);


        assertTrue(wasCreated);
        verify(statement).setString(1, "Lunes");
        verify(statement).setInt(4, 7);
    }

    @Test
    void findByProjectIdWhenRowsExistReturnsSchedules() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectScheduleDAO dao = new ProjectScheduleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "id", 2,
            "dia_semana", "Lunes",
            "hora_entrada", Time.valueOf("09:00:00"),
            "hora_salida", Time.valueOf("13:00:00"),
            "proyecto_id", 7
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<ProjectScheduleDTO> schedules = dao.findByProjectId(7);


            assertEquals(1, schedules.size());
            assertEquals(LocalTime.of(9, 0), schedules.get(0).getEntryTime());
        }
    }

    @Test
    void deleteByProjectIdWithConnectionReturnsTrueEvenWhenZeroRows()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectScheduleDAO dao = new ProjectScheduleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);


        boolean wasDeleted = dao.deleteByProjectId(7, connection);


        assertTrue(wasDeleted);
        verify(statement).setInt(1, 7);
    }

    private ProjectScheduleDTO buildSchedule() {
        return new ProjectScheduleDTO(
            2,
            "Lunes",
            LocalTime.of(9, 0),
            LocalTime.of(13, 0),
            7
        );
    }
}
