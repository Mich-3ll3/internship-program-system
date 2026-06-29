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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.ActivationTokenDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class ActivationTokenDAOTest {
    @Test
    void createWithConnectionWhenValidTokenReturnsTrue() throws Exception {
        
        ActivationTokenDTO token = buildToken();
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        when(connection.prepareStatement(org.mockito.ArgumentMatchers.anyString()))
            .thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        boolean wasCreated = dao.create(token, connection);

        assertTrue(wasCreated);
        verify(statement).setInt(1, token.getUserId());
        verify(statement).setString(2, token.getTokenHash());
        verify(statement).setTimestamp(3, token.getExpirationDate());
        verify(statement).setBoolean(4, token.isUsed());
    }

    @Test
    void findByTokenHashWhenExistsReturnsToken() throws Exception {
        
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = resultSet(row(
            "id", 3,
            "usuario_id", 9,
            "token_hash", "hash-token",
            "fecha_expiracion", Timestamp.valueOf("2026-06-05 10:00:00"),
            "usado", false,
            "fecha_uso", null
        ));
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ActivationTokenDTO> token = dao.findByTokenHash("hash-token");

            assertTrue(token.isPresent());
            assertEquals(3, token.get().getId());
            assertEquals(9, token.get().getUserId());
            assertEquals("hash-token", token.get().getTokenHash());
            assertFalse(token.get().isUsed());
        }
    }

    @Test
    void findByTokenHashWhenNotExistsReturnsEmpty() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet());

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<ActivationTokenDTO> token = dao.findByTokenHash("missing");


            assertTrue(token.isEmpty());
        }
    }

    @Test
    void markAsUsedWhenRowIsUpdatedReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasMarked = dao.markAsUsed(4);


            assertTrue(wasMarked);
            verify(statement).setInt(1, 4);
        }
    }

    @Test
    void invalidateTokensByUserIdWhenRowsAreUpdatedReturnsTrue()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(2);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wereInvalidated = dao.invalidateTokensByUserId(7);


            assertTrue(wereInvalidated);
            verify(statement).setInt(1, 7);
        }
    }

    @Test
    void markAsUsedWhenTokenIdIsInvalidThrowsBusinessException() {

        ActivationTokenDAO dao = new ActivationTokenDAO();


        assertThrows(BusinessException.class, () -> dao.markAsUsed(0));
    }

    @Test
    void createWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        ActivationTokenDTO token = buildToken();
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.create(token);


            assertFalse(wasCreated);
        }
    }

    @Test
    void markAsUsedWhenNoRowsAreAffectedReturnsFalse() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasMarked = dao.markAsUsed(4);


            assertFalse(wasMarked);
        }
    }

    @Test
    void invalidateTokensByUserIdWhenNoRowsAreAffectedReturnsFalse()
            throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ActivationTokenDAO dao = new ActivationTokenDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wereInvalidated = dao.invalidateTokensByUserId(7);


            assertFalse(wereInvalidated);
        }
    }

    @Test
    void createWhenTokenIsNullThrowsBusinessException() {

        ActivationTokenDAO dao = new ActivationTokenDAO();


        assertThrows(BusinessException.class, () -> dao.create(null));
    }

    @Test
    void findByTokenHashWhenHashIsBlankThrowsBusinessException() {

        ActivationTokenDAO dao = new ActivationTokenDAO();


        assertThrows(BusinessException.class, () -> dao.findByTokenHash(" "));
    }

    @Test
    void invalidateTokensByUserIdWhenUserIdIsInvalidThrowsBusinessException() {

        ActivationTokenDAO dao = new ActivationTokenDAO();


        assertThrows(BusinessException.class, () -> dao.invalidateTokensByUserId(0));
    }

    private ActivationTokenDTO buildToken() {
        ActivationTokenDTO token = new ActivationTokenDTO();
        token.setUserId(9);
        token.setTokenHash("hash-token");
        token.setExpirationDate(Timestamp.valueOf("2026-06-05 10:00:00"));
        token.setUsed(false);

        return token;
    }
}
