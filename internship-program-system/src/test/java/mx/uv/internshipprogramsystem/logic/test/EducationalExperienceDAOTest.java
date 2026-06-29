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
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class EducationalExperienceDAOTest {
    private Connection connection;
    private PreparedStatement statement;
    private EducationalExperienceDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
    }

    @Test
    void createWhenDataIsValidReturnsTrue() throws Exception {
        EducationalExperienceDTO experience = buildExperience();
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasCreated = dao.create(experience);

            assertTrue(wasCreated);
        }
    }

    @Test
    void findByNrcWhenExistsReturnsSchoolPeriod() throws Exception {
        when(statement.executeQuery()).thenReturn(
            resultSet(experienceRow("12345", "202651", "1", true))
        );

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            Optional<EducationalExperienceDTO> experience =
                dao.findByNrc("12345");

            assertEquals("202651", experience.orElseThrow().getSchoolPeriod());
        }
    }

    @Test
    void findAllWhenRowsExistReturnsSecondNrc() throws Exception {
        when(statement.executeQuery()).thenReturn(
            resultSet(
                experienceRow("12345", "202651", "1", true),
                experienceRow("67890", "202651", "2", false)
            )
        );

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<EducationalExperienceDTO> experiences = dao.findAll();

            assertEquals("67890", experiences.get(1).getNrc());
        }
    }

    @Test
    void createWhenNrcIsInvalidThrowsBusinessException() {
        EducationalExperienceDTO experience = buildExperience();
        experience.setNrc("12");

        assertThrows(BusinessException.class, () -> dao.create(experience));
    }

    private EducationalExperienceDTO buildExperience() {
        EducationalExperienceDTO experience =
            new EducationalExperienceDTO("12345", "202651", "1", 8, true);
        experience.setStartDate(LocalDate.of(2026, 2, 10));
        experience.setEndDate(LocalDate.of(2026, 7, 10));

        return experience;
    }

    private java.util.Map<String, Object> experienceRow(
            String nrc,
            String schoolPeriod,
            String section,
            boolean active
    ) {
        return row(
            "NRC", nrc,
            "periodo_escolar", schoolPeriod,
            "seccion", section,
            "profesor_id", 8,
            "activa", active,
            "fecha_inicio", java.sql.Date.valueOf("2026-02-10"),
            "fecha_fin", java.sql.Date.valueOf("2026-07-10"),
            "profesor_nombre", "Carlos Ruiz"
        );
    }
}
