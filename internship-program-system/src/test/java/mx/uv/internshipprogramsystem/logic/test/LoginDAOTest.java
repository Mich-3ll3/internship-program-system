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
import mx.uv.internshipprogramsystem.logic.dao.LoginDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;

class LoginDAOTest {
    @Test
    void loginWhenCredentialsAreValidReturnsStudentUser() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement loginStatement = mock(PreparedStatement.class);
        PreparedStatement resetStatement = mock(PreparedStatement.class);
        String passwordHash = new SecurityManager().hashPassword("Password123");
        LoginDAO dao = new LoginDAO();
        when(connection.prepareStatement(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(loginStatement, resetStatement);
        when(loginStatement.executeQuery()).thenReturn(resultSet(row(
            "id", 12,
            "correo_institucional", "ana@estudiantes.uv.mx",
            "contrasena", passwordHash,
            "nombre", "Ana",
            "apellido_paterno", "Lopez",
            "apellido_materno", "Diaz",
            "activo", true,
            "rol", "ESTUDIANTE",
            "intentos_fallidos_login", 0,
            "fecha_bloqueo_login", null,
            "matricula", "zS12345678",
            "numero_personal", null,
            "es_coordinador", false
        )));
        when(resetStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            UserDTO user = dao.login("ana@estudiantes.uv.mx", "Password123");


            assertTrue(user instanceof InternDTO);
            assertEquals(12, user.getId());
            assertEquals(UserRole.STUDENT, user.getRole());
            assertEquals("zS12345678", ((InternDTO) user).getEnrollmentNumber());
            verify(resetStatement).setInt(1, 12);
        }
    }

    @Test
    void loginWhenUserDoesNotExistThrowsBusinessException() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LoginDAO dao = new LoginDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            assertThrows(
                BusinessException.class,
                () -> dao.login("nadie@uv.mx", "Password123")
            );
        }
    }

    @Test
    void incrementFailedLoginAttemptsWhenRowIsUpdatedReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LoginDAO dao = new LoginDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasIncremented = dao.incrementFailedLoginAttempts(12);


            assertTrue(wasIncremented);
            verify(statement).setInt(1, 12);
        }
    }

    @Test
    void resetFailedLoginAttemptsWhenRowIsUpdatedReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LoginDAO dao = new LoginDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasReset = dao.resetFailedLoginAttempts(12);


            assertTrue(wasReset);
            verify(statement).setInt(1, 12);
        }
    }

    @Test
    void lockUserLoginWhenRowIsUpdatedReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LoginDAO dao = new LoginDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasLocked = dao.lockUserLogin(12);


            assertTrue(wasLocked);
            verify(statement).setInt(1, 12);
        }
    }
}
