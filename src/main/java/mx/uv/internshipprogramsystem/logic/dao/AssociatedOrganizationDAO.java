package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.AssociatedOrganization;

public class AssociatedOrganizationDAO {
    
    public int addAssociatedOrganizationDAO(AssociatedOrganization associatedOrganization) {
        int resultado = -1;
        String query = "INSERT INTO OrganizacionVinculada (nombre, direccion, ciudad, estado, correo, telefono, sector, noUsuariosIndirectos, noUsuariosDirectos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DataBaseManager.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(query)) {
            
            sentencia.setString(1, associatedOrganization.getName());
            sentencia.setString(2, associatedOrganization.getAddress());
            sentencia.setString(3, associatedOrganization.getCity());
            sentencia.setString(4, associatedOrganization.getState());
            sentencia.setString(5, associatedOrganization.getEmail());
            sentencia.setString(6,associatedOrganization.getPhoneNumber());
            sentencia.setString(7, associatedOrganization.getSector());

            resultado = sentencia.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("Error al registrar profesor: " + ex.getMessage());
            throw new RuntimeException("Error de conexión con la base de datos al registrar profesor.");
        }
        return resultado;
    }
}
    
