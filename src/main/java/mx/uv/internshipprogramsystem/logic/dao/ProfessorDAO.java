package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.Professor;
import mx.uv.internshipprogramsystem.logic.interfaces.IProfessorDAO;

public class ProfessorDAO implements IProfessorDAO {
    
    @Override
    public int addProfessor(Professor professor) {
        int resultado = -1;
        String query = "INSERT INTO Profesor (numeroPersonal, nombre, apellidos, correoInstitucional) VALUES (?, ?, ?, ?)";

        try (Connection conexion = DataBaseManager.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(query)) {
            
            sentencia.setInt(1, professor.getStaffNumber());
            sentencia.setString(2, professor.getNames());
            sentencia.setString(3, professor.getPaternalSurname());
            sentencia.setString(4, professor.getMaternalSurname());
            sentencia.setString(5, professor.getEmail());
            sentencia.setBoolean(6,professor.getIsCoordinator());

            resultado = sentencia.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("Error al registrar profesor: " + ex.getMessage());
            throw new RuntimeException("Error de conexión con la base de datos al registrar profesor.");
        }
        return resultado;
    }
}
