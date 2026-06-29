package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dao.ActivationTokenDAO;
import mx.uv.internshipprogramsystem.logic.dao.EducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dao.LoginDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectActivityDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dao.ProjectScheduleDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class DAOValidationAndEmptyResultTest {
    @Test
    void activationTokenCreateWhenTokenIsNullThrowsBusinessException() {
        // Arrange
        ActivationTokenDAO dao = new ActivationTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.create(null));
    }

    @Test
    void activationTokenFindByTokenHashWhenHashIsBlankThrowsBusinessException() {
        // Arrange
        ActivationTokenDAO dao = new ActivationTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByTokenHash(" "));
    }

    @Test
    void activationTokenInvalidateTokensByUserIdWhenIdIsInvalidThrowsBusinessException() {
        // Arrange
        ActivationTokenDAO dao = new ActivationTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.invalidateTokensByUserId(0));
    }

    @Test
    void educationalExperienceFindByNrcWhenBlankThrowsBusinessException() {
        // Arrange
        EducationalExperienceDAO dao = new EducationalExperienceDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByNrc(" "));
    }

    @Test
    void educationalExperienceFindByNrcWhenNotExistsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findByNrc("12345").isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void educationalExperienceFindAllWhenNoRowsReturnsEmptyList() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            List<EducationalExperienceDTO> experiences = dao.findAll();

            // Assert
            assertTrue(experiences.isEmpty());
        }
    }

    @Test
    void internFindByEnrollmentNumberWhenBlankThrowsBusinessException() {
        // Arrange
        InternDAO dao = new InternDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByEnrollmentNumber(" "));
    }

    @Test
    void internFindByEnrollmentNumberWhenNotExistsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        InternDAO dao = new InternDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findByEnrollmentNumber("zS12345678").isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void linkedOrganizationCreateWhenNoRowsAreAffectedReturnsFalse()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasCreated = dao.createLinkedOrganization(buildOrganization());

            // Assert
            assertFalse(wasCreated);
        }
    }

    @Test
    void linkedOrganizationFindAllWhenNoRowsReturnsEmptyList() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            List<LinkedOrganizationDTO> organizations = dao.findAll();

            // Assert
            assertTrue(organizations.isEmpty());
        }
    }

    @Test
    void loginWhenEmailIsEmptyThrowsBusinessException() {
        // Arrange
        LoginDAO dao = new LoginDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.login("", "Password123"));
    }

    @Test
    void loginWhenPasswordIsEmptyThrowsBusinessException() {
        // Arrange
        LoginDAO dao = new LoginDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.login("ana@uv.mx", ""));
    }

    @Test
    void professorFindByStaffNumberWhenBlankThrowsBusinessException() {
        // Arrange
        ProfessorDAO dao = new ProfessorDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByStaffNumber(" "));
    }

    @Test
    void professorFindByNameWhenBlankThrowsBusinessException() {
        // Arrange
        ProfessorDAO dao = new ProfessorDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByName(" "));
    }

    @Test
    void professorFindCoordinatorWhenNoRowsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProfessorDAO dao = new ProfessorDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findCoordinator().isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void projectActivityFindByProjectIdWhenNoRowsReturnsEmptyList()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectActivityDAO dao = new ProjectActivityDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findByProjectId(7).isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void projectFindByIdWhenNoRowsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findById(7).isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void projectCreateWhenNoRowsAreAffectedReturnsFalse() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectDAO dao = new ProjectDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasCreated = dao.create(buildProject());

            // Assert
            assertFalse(wasCreated);
        }
    }

    @Test
    void projectResponsibleFindByIdWhenNoRowsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findById(3).isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void projectResponsibleFindBySearchTextWhenBlankThrowsBusinessException() {
        // Arrange
        ProjectResponsibleDAO dao = new ProjectResponsibleDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findBySearchText(" "));
    }

    @Test
    void projectScheduleFindByProjectIdWhenNoRowsReturnsEmptyList()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ProjectScheduleDAO dao = new ProjectScheduleDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findByProjectId(7).isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void userFindByInstitutionalEmailWhenMissingReturnsEmpty()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean isEmpty = dao.findByInstitutionalEmail("ana@uv.mx").isEmpty();

            // Assert
            assertTrue(isEmpty);
        }
    }

    @Test
    void userUpdateWhenNoRowsAreAffectedReturnsFalse() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasUpdated = dao.update(buildUser());

            // Assert
            assertFalse(wasUpdated);
        }
    }

    @Test
    void userCountActiveUsersWhenNoRowReturnsZero() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            int total = dao.countActiveUsers();

            // Assert
            assertEquals(0, total);
        }
    }

    private LinkedOrganizationDTO buildOrganization() {
        return new LinkedOrganizationDTO(
            5,
            "Organizacion UV",
            "Av. Universidad",
            "Xalapa",
            "Veracruz",
            "contacto@uv.mx",
            "2281234567",
            "Educacion",
            20,
            10
        );
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

    private UserDTO buildUser() {
        UserDTO user = new UserDTO();
        user.setInstitutionalEmail("ana@uv.mx");
        user.setName("Ana");
        user.setFirstSurname("Lopez");
        user.setSecondSurname("Diaz");
        user.setIsActive(true);
        user.setRole(UserRole.PROFESSOR);

        return user;
    }
}
