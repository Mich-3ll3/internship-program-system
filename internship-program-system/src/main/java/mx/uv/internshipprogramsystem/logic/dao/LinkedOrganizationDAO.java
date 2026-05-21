package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.ILinkedOrganizationDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LinkedOrganizationDAO.class);

    private static final String INSERT_LINKED_ORGANIZATION_QUERY =
        "INSERT INTO ORGANIZACION_VINCULADA "
        + "(nombre, correo, telefono, direccion, estado, ciudad, sector, "
        + "numero_usuarios_indirectos, numero_usuarios_directos) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_LINKED_ORGANIZATIONS_QUERY =
        "SELECT id, nombre, direccion, ciudad, estado, correo, telefono, "
        + "sector, numero_usuarios_indirectos, numero_usuarios_directos "
        + "FROM ORGANIZACION_VINCULADA";

    private static final String UPDATE_LINKED_ORGANIZATION_QUERY =
        "UPDATE ORGANIZACION_VINCULADA SET nombre = ?, correo = ?, "
        + "telefono = ?, direccion = ?, estado = ?, ciudad = ?, "
        + "sector = ?, numero_usuarios_indirectos = ?, "
        + "numero_usuarios_directos = ? "
        + "WHERE id = ?";

    @Override
    public boolean createLinkedOrganization(
            LinkedOrganizationDTO linkedOrganization
    ) throws BusinessException {
        InputValidator.validateNotNull(
            linkedOrganization,
            "La organización vinculada no puede ser nula."
        );

        boolean wasCreated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertLinkedOrganizationStatement =
                 connection.prepareStatement(
                     INSERT_LINKED_ORGANIZATION_QUERY
                 )) {
            setLinkedOrganizationData(
                insertLinkedOrganizationStatement,
                linkedOrganization
            );

            wasCreated =
                insertLinkedOrganizationStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error creando organización vinculada {}",
                linkedOrganization.getName(),
                sqlException
            );

            throw new BusinessException(
                "Error creando organización vinculada: "
                    + linkedOrganization.getName(),
                sqlException
            );
        }

        return wasCreated;
    }

    @Override
    public List<LinkedOrganizationDTO> findAll()
            throws BusinessException {
        List<LinkedOrganizationDTO> linkedOrganizations =
            new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllLinkedOrganizationsStatement =
                 connection.prepareStatement(
                     SELECT_ALL_LINKED_ORGANIZATIONS_QUERY
                 );
             ResultSet resultSet =
                 selectAllLinkedOrganizationsStatement.executeQuery()) {
            while (resultSet.next()) {
                linkedOrganizations.add(
                    buildLinkedOrganization(resultSet)
                );
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error listando organizaciones vinculadas",
                sqlException
            );

            throw new BusinessException(
                "Error listando organizaciones vinculadas.",
                sqlException
            );
        }

        return List.copyOf(linkedOrganizations);
    }

    @Override
    public boolean update(LinkedOrganizationDTO linkedOrganization)
            throws BusinessException {
        InputValidator.validateNotNull(
            linkedOrganization,
            "La organización vinculada no puede ser nula."
        );

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateLinkedOrganizationStatement =
                 connection.prepareStatement(
                     UPDATE_LINKED_ORGANIZATION_QUERY
                 )) {
            setLinkedOrganizationData(
                updateLinkedOrganizationStatement,
                linkedOrganization
            );

            updateLinkedOrganizationStatement.setInt(
                10,
                linkedOrganization.getId()
            );

            wasUpdated =
                updateLinkedOrganizationStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error actualizando organización vinculada con id {}",
                linkedOrganization.getId(),
                sqlException
            );

            throw new BusinessException(
                "Error actualizando organización vinculada con id "
                    + linkedOrganization.getId(),
                sqlException
            );
        }

        return wasUpdated;
    }

    private void setLinkedOrganizationData(
            PreparedStatement statement,
            LinkedOrganizationDTO linkedOrganization
    ) throws SQLException {
        statement.setString(1, linkedOrganization.getName());
        statement.setString(2, linkedOrganization.getEmail());
        statement.setString(3, linkedOrganization.getPhoneNumber());
        statement.setString(4, linkedOrganization.getAddress());
        statement.setString(5, linkedOrganization.getState());
        statement.setString(6, linkedOrganization.getCity());
        statement.setString(7, linkedOrganization.getSector());
        statement.setInt(8, linkedOrganization.getIndirectUserCount());
        statement.setInt(9, linkedOrganization.getDirectUserCount());
    }

    private LinkedOrganizationDTO buildLinkedOrganization(
            ResultSet resultSet
    ) throws SQLException {
        LinkedOrganizationDTO linkedOrganization =
            new LinkedOrganizationDTO(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("direccion"),
                resultSet.getString("ciudad"),
                resultSet.getString("estado"),
                resultSet.getString("correo"),
                resultSet.getString("telefono"),
                resultSet.getString("sector"),
                resultSet.getInt("numero_usuarios_indirectos"),
                resultSet.getInt("numero_usuarios_directos")
            );

        return linkedOrganization;
    }
}