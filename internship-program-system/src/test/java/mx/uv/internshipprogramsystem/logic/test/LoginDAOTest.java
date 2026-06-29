package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import mx.uv.internshipprogramsystem.logic.dao.LoginDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LoginDAOTest {
    private Connection connection;
    private PreparedStatement statement;
    private LoginDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        dao = new LoginDAO();
        mockPreparedStatement(connection, statement);
    }

    @Test
    void loginWhenCredentialsAreValidReturnsStudentEnrollment()
            throws Exception {
        PreparedStatement resetStatement = mock(PreparedStatement.class);
        String passwordHash =
            new SecurityManager().hashPassword("Password123");
        when(connection.prepareStatement(anyString()))
            .thenReturn(statement, resetStatement);
        when(statement.executeQuery()).thenReturn(resultSet(studentRow(passwordHash)));
        when(resetStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            UserDTO user = dao.login("ana@estudiantes.uv.mx", "Password123");

            assertEquals(
                "S12345678",
                ((InternDTO) user).getEnrollmentNumber()
            );
        }
    }

    @Test
    void loginWhenUserDoesNotExistThrowsBusinessException()
            throws Exception {
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
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasIncremented = dao.incrementFailedLoginAttempts(12);

            assertTrue(wasIncremented);
        }
    }

    @Test
    void resetFailedLoginAttemptsWhenRowIsUpdatedReturnsTrue()
            throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasReset = dao.resetFailedLoginAttempts(12);

            assertTrue(wasReset);
        }
    }

    @Test
    void lockUserLoginWhenRowIsUpdatedReturnsTrue() throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasLocked = dao.lockUserLogin(12);

            assertTrue(wasLocked);
        }
    }

    private java.util.Map<String, Object> studentRow(String passwordHash) {
        return row(
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
            "matricula", "S12345678",
            "numero_personal", null,
            "es_coordinador", false
        );
    }
}
