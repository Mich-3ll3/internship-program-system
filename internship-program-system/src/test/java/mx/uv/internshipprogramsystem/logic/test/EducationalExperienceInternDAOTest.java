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
import java.time.LocalDate;
import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceInternDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternStatus;

class EducationalExperienceInternDAOTest {
    @Test
    void createWhenAssignmentIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceInternDTO assignment = buildAssignment();
        EducationalExperienceInternDAO dao = new EducationalExperienceInternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(assignment);


            assertTrue(wasCreated);
            verify(statement).setString(1, "12345");
            verify(statement).setInt(2, 10);
            verify(statement).setBoolean(4, true);
            verify(statement).setInt(5, 1);
            verify(statement).setString(6, "ACTIVA");
        }
    }

    @Test
    void existsAssignmentWhenCountIsGreaterThanZeroReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceInternDAO dao = new EducationalExperienceInternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 1)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean exists = dao.existsAssignment(buildAssignment());


            assertTrue(exists);
            verify(statement).setString(1, "12345");
            verify(statement).setInt(2, 10);
        }
    }

    @Test
    void existsActiveAssignmentByInternIdWhenCountIsPositiveReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceInternDAO dao = new EducationalExperienceInternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 1)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean exists = dao.existsActiveAssignmentByInternId(10);


            assertTrue(exists);
            verify(statement).setInt(1, 10);
        }
    }

    @Test
    void existsActiveEducationalExperienceByNrcWhenCountIsPositiveReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceInternDAO dao = new EducationalExperienceInternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 1)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean exists = dao.existsActiveEducationalExperienceByNrc("12345");


            assertTrue(exists);
            verify(statement).setString(1, "12345");
        }
    }

    @Test
    void countValidOpportunitiesByInternIdReturnsTotal() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceInternDAO dao = new EducationalExperienceInternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("total", 2)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            int total = dao.countValidOpportunitiesByInternId(10);


            assertEquals(2, total);
            verify(statement).setInt(1, 10);
        }
    }

    private EducationalExperienceInternDTO buildAssignment() {
        EducationalExperienceInternDTO assignment =
            new EducationalExperienceInternDTO(
                "12345",
                10,
                LocalDate.of(2026, 6, 4),
                true,
                1,
                EducationalExperienceInternStatus.ACTIVA
            );

        return assignment;
    }
}
