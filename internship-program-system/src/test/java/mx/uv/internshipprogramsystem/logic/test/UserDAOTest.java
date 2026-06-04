package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.generatedKeys;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatementWithGeneratedKeys;
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
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class UserDAOTest {
    @Test
    void createWithConnectionWhenUserIsValidReturnsGeneratedId()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatementWithGeneratedKeys(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);
        when(statement.getGeneratedKeys()).thenReturn(generatedKeys(12));


        int generatedId = dao.create(buildUser(), connection);


        assertEquals(12, generatedId);
        verify(statement).setString(1, "ana@uv.mx");
        verify(statement).setString(5, "PROFESOR");
    }

    @Test
    void updateWhenUserIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildUser());


            assertTrue(wasUpdated);
            verify(statement).setString(1, "Ana");
            verify(statement).setString(6, "ana@uv.mx");
        }
    }

    @Test
    void findByInstitutionalEmailWhenExistsReturnsUser() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "id", 12,
            "correo_institucional", "ana@uv.mx",
            "nombre", "Ana",
            "apellido_paterno", "Lopez",
            "apellido_materno", "Diaz",
            "activo", true,
            "rol", "PROFESOR"
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            UserDTO user = dao.findByInstitutionalEmail("ana@uv.mx");


            assertEquals(12, user.getId());
            assertEquals(UserRole.PROFESSOR, user.getRole());
        }
    }

    @Test
    void changeStatusWhenRowIsUpdatedReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasChanged = dao.changeStatus(12, false);


            assertTrue(wasChanged);
            verify(statement).setBoolean(1, false);
            verify(statement).setInt(2, 12);
        }
    }

    @Test
    void countActiveUsersReturnsTotal() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row("totalActivos", 8)));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            int total = dao.countActiveUsers();


            assertEquals(8, total);
        }
    }

    @Test
    void activateAccountWhenRowIsUpdatedReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        UserDAO dao = new UserDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasActivated = dao.activateAccount(12, "password-hash");


            assertTrue(wasActivated);
            verify(statement).setString(1, "password-hash");
            verify(statement).setInt(2, 12);
        }
    }

    @Test
    void createWhenEmailIsInvalidThrowsBusinessException() {

        UserDTO user = buildUser();
        user.setInstitutionalEmail("ana@gmail.com");
        UserDAO dao = new UserDAO();


        assertThrows(BusinessException.class, () -> dao.create(user, mock(Connection.class)));
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
