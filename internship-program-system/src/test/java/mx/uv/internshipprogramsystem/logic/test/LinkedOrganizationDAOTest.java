package mx.uv.internshipprogramsystem.logic.test;

import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockDataBaseConnection;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.mockPreparedStatement;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.resultSet;
import static mx.uv.internshipprogramsystem.logic.test.DaoTestSupport.row;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dao.LinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LinkedOrganizationDAOTest {
    private Connection connection;
    private PreparedStatement statement;
    private LinkedOrganizationDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        dao = new LinkedOrganizationDAO();
        mockPreparedStatement(connection, statement);
    }

    @Test
    void createLinkedOrganizationWhenDataIsValidReturnsTrue()
            throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasCreated =
                dao.createLinkedOrganization(buildOrganization());

            assertTrue(wasCreated);
        }
    }

    @Test
    void findAllWhenRowsExistReturnsOrganizationCountry()
            throws Exception {
        when(statement.executeQuery()).thenReturn(
            resultSet(organizationRow())
        );

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            List<LinkedOrganizationDTO> organizations = dao.findAll();

            assertEquals("Mexico", organizations.get(0).getCountry());
        }
    }

    @Test
    void updateWhenDataIsValidReturnsTrue() throws Exception {
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<?> ignored = mockDataBaseConnection(connection)) {
            boolean wasUpdated = dao.update(buildOrganization());

            assertTrue(wasUpdated);
        }
    }

    @Test
    void updateWhenOrganizationIsNullThrowsBusinessException() {
        assertThrows(BusinessException.class, () -> dao.update(null));
    }

    private LinkedOrganizationDTO buildOrganization() {
        return new LinkedOrganizationDTO(
            5,
            "Organizacion UV",
            "Av. Universidad",
            "Mexico",
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
            "pais", "Mexico",
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
