package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.ILinkedOrganizationDAO;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO{
    private static final Logger logger = LoggerFactory.getLogger(LinkedOrganizationDAO.class);

    public LinkedOrganizationDAO() {
    }

    public boolean createLikendOrganization(LinkedOrganizationDTO linkedOrganization) throws BusinessException {
        String insertLinkedOrganizationQuery = 
            "INSERT INTO ORGANIZACION_VINCULADA " +
            "(nombre, correo, telefono, direccion, estado, ciudad, sector, numero_usuarios_indirectos, numero_usuarios_directos) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertLinkedOrganizationStatement = connection.prepareStatement(insertLinkedOrganizationQuery)) {
            insertLinkedOrganizationStatement.setString(1, linkedOrganization.getName());
            insertLinkedOrganizationStatement.setString(2, linkedOrganization.getEmail());
            insertLinkedOrganizationStatement.setString(3, linkedOrganization.getPhoneNumber());
            insertLinkedOrganizationStatement.setString(4, linkedOrganization.getAddress());
            insertLinkedOrganizationStatement.setString(5, linkedOrganization.getState());
            insertLinkedOrganizationStatement.setString(6, linkedOrganization.getCity());
            insertLinkedOrganizationStatement.setString(7, linkedOrganization.getSector());
            insertLinkedOrganizationStatement.setInt(8, linkedOrganization.getIndirectUserCount());
            insertLinkedOrganizationStatement.setInt(9, linkedOrganization.getDirectUserCount());
            return insertLinkedOrganizationStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error creando organización vinculada: " + linkedOrganization.getName(), sqlException);
        }
    }

    public List<LinkedOrganizationDTO> findAll() throws BusinessException {
        String selectAllLinkedOrganizationsQuery = "SELECT * FROM ORGANIZACION_VINCULADA";
        List<LinkedOrganizationDTO> linkedOrganizations = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectAllLinkedOrganizationsStatement = connection.prepareStatement(selectAllLinkedOrganizationsQuery);
             ResultSet resultSet = selectAllLinkedOrganizationsStatement.executeQuery(selectAllLinkedOrganizationsQuery)) {

            while (resultSet.next()) {
                linkedOrganizations.add(new LinkedOrganizationDTO(
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
                ));
            }
            return linkedOrganizations;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error listando organizaciones vinculadas", sqlException);
        }
    }

    public boolean update(LinkedOrganizationDTO linkedOrganization) throws BusinessException {
        String updateLinkedOrganizationQuery = 
            "UPDATE ORGANIZACION_VINCULADA SET nombre=?, correo=?, telefono=?, direccion=?, estado=?, ciudad=?, sector=?, numero_usuarios_indirectos=?, numero_usuarios_directos=? WHERE id=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateLinkedOrganizationStatement = connection.prepareStatement(updateLinkedOrganizationQuery)) {
            updateLinkedOrganizationStatement.setString(1, linkedOrganization.getName());
            updateLinkedOrganizationStatement.setString(2, linkedOrganization.getEmail());
            updateLinkedOrganizationStatement.setString(3, linkedOrganization.getPhoneNumber());
            updateLinkedOrganizationStatement.setString(4, linkedOrganization.getAddress());
            updateLinkedOrganizationStatement.setString(5, linkedOrganization.getState());
            updateLinkedOrganizationStatement.setString(6, linkedOrganization.getCity());
            updateLinkedOrganizationStatement.setString(7, linkedOrganization.getSector());
            updateLinkedOrganizationStatement.setInt(8, linkedOrganization.getIndirectUserCount());
            updateLinkedOrganizationStatement.setInt(9, linkedOrganization.getDirectUserCount());
            updateLinkedOrganizationStatement.setInt(10, linkedOrganization.getId());
            return updateLinkedOrganizationStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error actualizando organización vinculada con id=" + linkedOrganization.getId(), sqlException);
        }
    }
}