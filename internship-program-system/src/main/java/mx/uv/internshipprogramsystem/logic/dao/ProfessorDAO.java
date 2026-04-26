package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProfessorDAO;

public class ProfessorDAO implements IProfessorDAO{
    private static final Logger logger = LoggerFactory.getLogger(ProfessorDAO.class);

    public ProfessorDAO() {
    }

    public boolean createProfessor(int staffNumber, boolean isCoordinator, int userId) throws BusinessException {
        String insertProfessorQuery = "INSERT INTO PROFESOR (numero_personal, es_coordinador, id_usuario) VALUES (?,?,?)";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertProfessorStatement = connection.prepareStatement(insertProfessorQuery)) {
            insertProfessorStatement.setInt(1, staffNumber);
            insertProfessorStatement.setBoolean(2, isCoordinator);
            insertProfessorStatement.setInt(3, userId);
            return insertProfessorStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error creando datos de profesor con número de personal " + staffNumber, sqlException);
        }
    }

    public boolean update(ProfessorDTO professor) throws BusinessException {
        String updateProfessorQuery = "UPDATE PROFESOR SET numero_personal=?, es_coordinador=? WHERE id_usuario=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateProfessorStatement = connection.prepareStatement(updateProfessorQuery)) {
            updateProfessorStatement.setInt(1, professor.getStaffNumber());
            updateProfessorStatement.setBoolean(2, professor.getIsCoordinator());
            updateProfessorStatement.setInt(3, professor.getId());
            return updateProfessorStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error actualizando profesor con número de personal " + professor.getStaffNumber(), sqlException);
        }
    }

    public ProfessorDTO findByStaffNumber(String staffNumber) throws BusinessException {
        String selectProfessorByStaffNumberQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p " +
            "JOIN USUARIO u ON p.id_usuario = u.id " +
            "WHERE p.numero_personal = ?";
        ProfessorDTO professor = new ProfessorDTO();
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectProfessorByStaffNumberStatement = connection.prepareStatement(selectProfessorByStaffNumberQuery)) {
            selectProfessorByStaffNumberStatement.setString(1, staffNumber);
            try (ResultSet resultSet = selectProfessorByStaffNumberStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProfessorDTO(//+ de dos returns
                        resultSet.getInt("numero_personal"),
                        resultSet.getBoolean("es_coordinador"),
                        resultSet.getInt("id"),
                        resultSet.getString("correo_institucional"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_paterno"),
                        resultSet.getString("apellido_materno"),
                        resultSet.getBoolean("activo"),
                        RolUsuario.valueOf(resultSet.getString("rol"))
                    );
                }
            }
        } catch (SQLException sqlException) {
            throw new BusinessException("Error buscando profesor con número de personal " + staffNumber, sqlException);
        }
        return professor;
    }

    public List<ProfessorDTO> findAll() throws BusinessException {
        String selectAllProfessorsQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p " +
            "JOIN USUARIO u ON p.id_usuario = u.id";
        List<ProfessorDTO> professors = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                Statement selectAllProfessorsStatement = connection.createStatement();
             ResultSet resultSet = selectAllProfessorsStatement.executeQuery(selectAllProfessorsQuery)) {

            while (resultSet.next()) {
                professors.add(new ProfessorDTO(
                    resultSet.getInt("numero_personal"),
                    resultSet.getBoolean("es_coordinador"),
                    resultSet.getInt("id"),
                    resultSet.getString("correo_institucional"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido_paterno"),
                    resultSet.getString("apellido_materno"),
                    resultSet.getBoolean("activo"),
                    RolUsuario.valueOf(resultSet.getString("rol"))
                ));
            }
            return professors;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error listando profesores", sqlException);
        }
    }

    public ProfessorDTO findCoordinator() throws BusinessException {
        String selectCoordinatorQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p " +
            "JOIN USUARIO u ON p.id_usuario = u.id " +
            "WHERE p.es_coordinador = true";
        try (Connection connection = DataBaseManager.getConnection();
                Statement selectCoordinatorStatement = connection.createStatement();
             ResultSet resultSet = selectCoordinatorStatement.executeQuery(selectCoordinatorQuery)) {

            if (resultSet.next()) {
                return new ProfessorDTO(
                    resultSet.getInt("numero_personal"),
                    resultSet.getBoolean("es_coordinador"),
                    resultSet.getInt("id"),
                    resultSet.getString("correo_institucional"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellido_paterno"),
                    resultSet.getString("apellido_materno"),
                    resultSet.getBoolean("activo"),
                    RolUsuario.valueOf(resultSet.getString("rol"))
                );
            }
            return null;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error obteniendo coordinador", sqlException);
        }
    }

    public List<ProfessorDTO> findByStatus(boolean isActive) throws BusinessException {
        String selectProfessorsByStatusQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p JOIN USUARIO u ON p.id_usuario = u.id " +
            "WHERE u.activo = ?";
        List<ProfessorDTO> professors = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectProfessorsByStatusStatement = connection.prepareStatement(selectProfessorsByStatusQuery)) {
            selectProfessorsByStatusStatement.setBoolean(1, isActive);
            try (ResultSet resultSet = selectProfessorsByStatusStatement.executeQuery()) {
                while (resultSet.next()) {
                    professors.add(new ProfessorDTO(
                        resultSet.getInt("numero_personal"),
                        resultSet.getBoolean("es_coordinador"),
                        resultSet.getInt("id"),
                        resultSet.getString("correo_institucional"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_paterno"),
                        resultSet.getString("apellido_materno"),
                        resultSet.getBoolean("activo"),
                        RolUsuario.valueOf(resultSet.getString("rol"))
                    ));
                }
            }
            return professors;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error listando profesores activos/inactivos", sqlException);
        }
    }
}
