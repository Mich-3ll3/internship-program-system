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
import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class EducationalExperienceDAOTest {
    @Test
    void createWhenDataIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDTO experience =
            new EducationalExperienceDTO("12345", "2026-51", "1", 8, true);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(experience);


            assertTrue(wasCreated);
            verify(statement).setString(1, "12345");
            verify(statement).setString(2, "2026-51");
            verify(statement).setString(3, "1");
            verify(statement).setInt(4, 8);
            verify(statement).setBoolean(5, true);
        }
    }

    @Test
    void findByNrcWhenExistsReturnsExperience() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "NRC", "12345",
            "periodo_escolar", "2026-51",
            "seccion", "1",
            "profesor_id", 8,
            "activa", true
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<EducationalExperienceDTO> experience = dao.findByNrc("12345");


            assertTrue(experience.isPresent());
            assertEquals("12345", experience.get().getNrc());
            assertEquals("2026-51", experience.get().getSchoolPeriod());
        }
    }

    @Test
    void findAllWhenRowsExistReturnsList() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(
            row("NRC", "12345", "periodo_escolar", "2026-51",
                "seccion", "1", "profesor_id", 8, "activa", true),
            row("NRC", "67890", "periodo_escolar", "2026-51",
                "seccion", "2", "profesor_id", 9, "activa", false)
        ));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<EducationalExperienceDTO> experiences = dao.findAll();


            assertEquals(2, experiences.size());
            assertEquals("67890", experiences.get(1).getNrc());
        }
    }

    @Test
    void createWhenNrcIsInvalidThrowsBusinessException() {

        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        EducationalExperienceDTO experience =
            new EducationalExperienceDTO("12", "2026-51", "1", 8, true);


        assertThrows(BusinessException.class, () -> dao.create(experience));
    }

    @Test
    void createWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDTO experience =
            new EducationalExperienceDTO("12345", "2026-51", "1", 8, true);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(experience);


            assertFalse(wasCreated);
        }
    }

    @Test
    void findByNrcWhenBlankThrowsBusinessException() {

        EducationalExperienceDAO dao = new EducationalExperienceDAO();


        assertThrows(BusinessException.class, () -> dao.findByNrc(" "));
    }

    @Test
    void findByNrcWhenNotExistsReturnsEmpty() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<EducationalExperienceDTO> experience = dao.findByNrc("12345");


            assertTrue(experience.isEmpty());
        }
    }

    @Test
    void findAllWhenNoRowsReturnsEmptyList() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<EducationalExperienceDTO> experiences = dao.findAll();


            assertTrue(experiences.isEmpty());
        }
    }
}
