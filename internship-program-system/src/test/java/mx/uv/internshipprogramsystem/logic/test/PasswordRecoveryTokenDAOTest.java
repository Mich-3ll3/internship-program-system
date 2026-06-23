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
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dao.PasswordRecoveryTokenDAO;
import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class PasswordRecoveryTokenDAOTest {
    @Test
    void createWhenValidTokenReturnsTrue() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        PasswordRecoveryTokenDTO token = buildToken();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasCreated = dao.create(token);

            // Assert
            assertTrue(wasCreated);
            verify(statement).setInt(1, token.getUserId());
            verify(statement).setString(2, token.getTokenHash());
            verify(statement).setTimestamp(3, token.getExpirationDate());
            verify(statement).setBoolean(4, token.isUsed());
        }
    }

    @Test
    void createWhenNoRowsAreAffectedReturnsFalse() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasCreated = dao.create(buildToken());

            // Assert
            assertFalse(wasCreated);
        }
    }

    @Test
    void createWhenTokenIsNullThrowsBusinessException() {
        // Arrange
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.create(null));
    }

    @Test
    void findByTokenHashWhenExistsReturnsToken() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = resultSet(row(
            "id", 6,
            "usuario_id", 15,
            "token_hash", "recovery-hash",
            "fecha_expiracion", Timestamp.valueOf("2026-06-05 10:00:00"),
            "usado", false,
            "fecha_uso", null
        ));
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            Optional<PasswordRecoveryTokenDTO> token =
                dao.findByTokenHash("recovery-hash");

            // Assert
            assertTrue(token.isPresent());
            assertEquals(6, token.get().getId());
            assertEquals(15, token.get().getUserId());
            assertEquals("recovery-hash", token.get().getTokenHash());
            assertFalse(token.get().isUsed());
        }
    }

    @Test
    void findByTokenHashWhenNotExistsReturnsEmpty() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            Optional<PasswordRecoveryTokenDTO> token = dao.findByTokenHash("missing");

            // Assert
            assertTrue(token.isEmpty());
        }
    }

    @Test
    void findByTokenHashWhenHashIsEmptyThrowsBusinessException() {
        // Arrange
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.findByTokenHash(""));
    }

    @Test
    void markAsUsedWhenRowIsUpdatedReturnsTrue() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasMarked = dao.markAsUsed(6);

            // Assert
            assertTrue(wasMarked);
            verify(statement).setInt(1, 6);
        }
    }

    @Test
    void markAsUsedWhenNoRowsAreAffectedReturnsFalse() throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wasMarked = dao.markAsUsed(6);

            // Assert
            assertFalse(wasMarked);
        }
    }

    @Test
    void markAsUsedWhenTokenIdIsInvalidThrowsBusinessException() {
        // Arrange
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.markAsUsed(0));
    }

    @Test
    void invalidateTokensByUserIdWhenRowsAreUpdatedReturnsTrue()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(2);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wereInvalidated = dao.invalidateTokensByUserId(15);

            // Assert
            assertTrue(wereInvalidated);
            verify(statement).setInt(1, 15);
        }
    }

    @Test
    void invalidateTokensByUserIdWhenNoRowsAreAffectedReturnsFalse()
            throws Exception {
        // Arrange
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            // Act
            boolean wereInvalidated = dao.invalidateTokensByUserId(15);

            // Assert
            assertFalse(wereInvalidated);
        }
    }

    @Test
    void invalidateTokensByUserIdWhenUserIdIsInvalidThrowsBusinessException() {
        // Arrange
        PasswordRecoveryTokenDAO dao = new PasswordRecoveryTokenDAO();

        // Act & Assert
        assertThrows(BusinessException.class, () -> dao.invalidateTokensByUserId(0));
    }

    private PasswordRecoveryTokenDTO buildToken() {
        PasswordRecoveryTokenDTO token = new PasswordRecoveryTokenDTO();
        token.setUserId(15);
        token.setTokenHash("recovery-hash");
        token.setExpirationDate(Timestamp.valueOf("2026-06-05 10:00:00"));
        token.setUsed(false);

        return token;
    }
}
