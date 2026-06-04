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
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

class LinkedOrganizationDAOTest {
    @Test
    void createLinkedOrganizationWhenDataIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasCreated = dao.createLinkedOrganization(buildOrganization());


            assertTrue(wasCreated);
            verify(statement).setString(1, "Organizacion UV");
            verify(statement).setInt(8, 20);
            verify(statement).setInt(9, 10);
        }
    }

    @Test
    void findAllWhenRowsExistReturnsOrganizations() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeQuery()).thenReturn(resultSet(organizationRow()));

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            List<LinkedOrganizationDTO> organizations = dao.findAll();


            assertEquals(1, organizations.size());
            assertEquals("Organizacion UV", organizations.get(0).getName());
        }
    }

    @Test
    void updateWhenDataIsValidReturnsTrue() throws Exception {

        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {

            boolean wasUpdated = dao.update(buildOrganization());


            assertTrue(wasUpdated);
            verify(statement).setInt(10, 5);
        }
    }

    @Test
    void updateWhenOrganizationIsNullThrowsBusinessException() {

        LinkedOrganizationDAO dao = new LinkedOrganizationDAO();


        assertThrows(BusinessException.class, () -> dao.update(null));
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

    private java.util.Map<String, Object> organizationRow() {
        return row(
            "id", 5,
            "nombre", "Organizacion UV",
            "direccion", "Av. Universidad",
            "ciudad", "Xalapa",
            "estado", "Veracruz",
            "correo", "contacto@uv.mx",
            "telefono", "2281234567",
            "sector", "Educacion",
            "numero_usuarios_indirectos", 20,
            "numero_usuarios_directos", 10
        );
    }
}
