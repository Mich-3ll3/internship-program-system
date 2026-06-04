package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.SelfAssessmentDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;

class SelfAssessmentDAOTest {
    @Test
    void getAllSelfAssessmentsWhenRowsExistReturnsAssessments()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        SelfAssessmentDAO dao = new SelfAssessmentDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(assessmentRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<SelfAssessmentDTO> assessments = dao.getAllSelfAssessments();


            assertEquals(1, assessments.size());
            assertEquals("Ana Lopez Diaz", assessments.get(0).getStudentName());
            assertEquals(5, assessments.get(0).getAfirmacion5());
        }
    }

    @Test
    void insertWhenAssessmentIsValidExecutesInsert() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        SelfAssessmentDAO dao = new SelfAssessmentDAO();
        mockPreparedStatement(connection, statement);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            dao.insert(buildAssessment());


            verify(statement).setInt(1, 12);
            verify(statement).setString(5, "Sistemas");
            verify(statement).setInt(17, 10);
            verify(statement).setString(18, "Observaciones");
            verify(statement).executeUpdate();
        }
    }

    private SelfAssessmentDTO buildAssessment() {
        SelfAssessmentDTO assessment = new SelfAssessmentDTO();
        assessment.setStudentId(12);
        assessment.setProjectId(7);
        assessment.setOrganizationId(5);
        assessment.setResponsibleId(3);
        assessment.setDepartment("Sistemas");
        assessment.setPlace("Xalapa");
        assessment.setDate(LocalDate.of(2026, 6, 4));
        assessment.setAfirmacion1(1);
        assessment.setAfirmacion2(2);
        assessment.setAfirmacion3(3);
        assessment.setAfirmacion4(4);
        assessment.setAfirmacion5(5);
        assessment.setAfirmacion6(6);
        assessment.setAfirmacion7(7);
        assessment.setAfirmacion8(8);
        assessment.setAfirmacion9(9);
        assessment.setAfirmacion10(10);
        assessment.setObservations("Observaciones");

        return assessment;
    }

    private java.util.Map<String, Object> assessmentRow() {
        return row(
            "id", 1,
            "estudiante_id", 12,
            "proyecto_id", 7,
            "organizacion_id", 5,
            "responsable_id", 3,
            "fecha", java.sql.Date.valueOf("2026-06-04"),
            "departamento", "Sistemas",
            "lugar", "Xalapa",
            "observaciones", "Observaciones",
            "afirmacion1", 1,
            "afirmacion2", 2,
            "afirmacion3", 3,
            "afirmacion4", 4,
            "afirmacion5", 5,
            "afirmacion6", 6,
            "afirmacion7", 7,
            "afirmacion8", 8,
            "afirmacion9", 9,
            "afirmacion10", 10,
            "studentName", "Ana Lopez Diaz",
            "projectName", "Sistema",
            "responsibleName", "Carlos Sanchez Ramos",
            "organizationName", "Organizacion UV"
        );
    }
}
