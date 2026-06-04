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
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dao.DocumentDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.DocumentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class DocumentDAOTest {
    @Test
    void insertWhenDocumentIsValidReturnsTrueAndAssignsId() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        DocumentDTO document = new DocumentDTO("Carta", "PDF", "/docs/carta.pdf");
        DocumentDAO dao = new DocumentDAO();
        mockPreparedStatementWithGeneratedKeys(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);
        when(statement.getGeneratedKeys()).thenReturn(generatedKeys(15));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasInserted = dao.insert(document);


            assertTrue(wasInserted);
            assertEquals(15, document.getId());
            verify(statement).setString(1, "Carta");
            verify(statement).setString(2, "PDF");
            verify(statement).setString(3, "/docs/carta.pdf");
        }
    }

    @Test
    void findByIdWhenDocumentExistsReturnsDocument() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        DocumentDAO dao = new DocumentDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(row(
            "id", 15,
            "nombre", "Carta",
            "tipo", "PDF",
            "ruta", "/docs/carta.pdf"
        )));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            Optional<DocumentDTO> document = dao.findById(15);


            assertTrue(document.isPresent());
            assertEquals("Carta", document.get().getName());
            assertEquals("PDF", document.get().getType());
            assertEquals("/docs/carta.pdf", document.get().getPath());
        }
    }

    @Test
    void deleteWhenDocumentExistsReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        DocumentDAO dao = new DocumentDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasDeleted = dao.delete(15);


            assertTrue(wasDeleted);
            verify(statement).setInt(1, 15);
        }
    }

    @Test
    void findByIdWhenIdIsInvalidThrowsBusinessException() {

        DocumentDAO dao = new DocumentDAO();


        assertThrows(BusinessException.class, () -> dao.findById(0));
    }
}
