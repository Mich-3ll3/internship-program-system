package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.RolUsuario;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProfessorDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;

public class ProfessorDAO implements IProfessorDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorDAO.class);

    public ProfessorDAO() {}

    public boolean create(ProfessorDTO professor) throws BusinessException {
        InputValidator.validateNotNull(professor, "ProfessorDTO no puede ser nulo.");
        ProfessorValidator validator = new ProfessorValidator();
        validator.validateStaffNumber(professor.getStaffNumber());

        String insertProfessorQuery = "INSERT INTO PROFESOR (numero_personal, es_coordinador, usuario_id) VALUES (?,?,?)";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertProfessorStatement = connection.prepareStatement(insertProfessorQuery)) {

            insertProfessorStatement.setInt(1, professor.getStaffNumber());
            insertProfessorStatement.setBoolean(2, professor.getIsCoordinator());
            insertProfessorStatement.setInt(3, professor.getId());

            return insertProfessorStatement.executeUpdate() > 0;

        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo establecer conexión con la base de datos.", connectionException);
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad: número de personal duplicado", integrityException);
            throw new BusinessException("El número de personal ya existe.", integrityException);
        } catch (SQLException insertException) {
            LOGGER.error("Error SQL al insertar profesor", insertException);
            throw new BusinessException("Error al insertar el profesor en la base de datos.", insertException);
        }
    }

    public boolean update(ProfessorDTO professor) throws BusinessException {
        InputValidator.validateNotNull(professor, "ProfessorDTO no puede ser nulo.");
        ProfessorValidator validator = new ProfessorValidator();
        validator.validateStaffNumber(professor.getStaffNumber());

        String updateProfessorQuery = "UPDATE PROFESOR SET numero_personal=?, es_coordinador=? WHERE usuario_id=?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateProfessorStatement = connection.prepareStatement(updateProfessorQuery)) {

            updateProfessorStatement.setInt(1, professor.getStaffNumber());
            updateProfessorStatement.setBoolean(2, professor.getIsCoordinator());
            updateProfessorStatement.setInt(3, professor.getId());

            return updateProfessorStatement.executeUpdate() > 0;

        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad al actualizar profesor", integrityException);
            throw new BusinessException("El número de personal ya existe o el usuario asociado no es válido.", integrityException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al actualizar profesor {}", professor.getStaffNumber(), sqlException);
            throw new BusinessException("Error actualizando profesor con número de personal " + professor.getStaffNumber(), sqlException);
        }
    }

    public ProfessorDTO findByStaffNumber(String staffNumber) throws BusinessException {
        InputValidator.validateNotEmpty(staffNumber, "El número de personal no puede estar vacío.");
        
        String selectProfessorByStaffNumberQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id " +
            "WHERE p.numero_personal = ?";
        
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectProfessorByStaffNumberStatement = connection.prepareStatement(selectProfessorByStaffNumberQuery)) {

            selectProfessorByStaffNumberStatement.setString(1, staffNumber);
            
            try (ResultSet resultSet = selectProfessorByStaffNumberStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProfessorDTO(
                        resultSet.getInt("numero_personal"),
                        resultSet.getBoolean("es_coordinador"),
                        resultSet.getInt("id"),
                        resultSet.getString("correo_institucional"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido_paterno"),
                        resultSet.getString("apellido_materno"),
                        resultSet.getBoolean("activo")
                    );
                }
            }
            return null;

        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException selectException) {
            throw new BusinessException("Error buscando profesor con número de personal " + staffNumber, selectException);
        }
    }

    public List<ProfessorDTO> findAll() throws BusinessException {
        String selectAllProfessorsQuery =
            "SELECT u.nombre " +
            "FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id";
        List<ProfessorDTO> professors = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllProfessorsStatement = connection.prepareStatement(selectAllProfessorsQuery);
             ResultSet resultSet = selectAllProfessorsStatement.executeQuery()) {

            while (resultSet.next()) {
                ProfessorDTO professor = new ProfessorDTO();
                professor.setName(resultSet.getString("nombre"));
                professors.add(professor);
            }
            return professors;

        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al obtener la lista de profesores", sqlException);
            throw new BusinessException("Error obteniendo la lista de profesores", sqlException);
        }
    }

    public ProfessorDTO findCoordinator() throws BusinessException {
        String selectCoordinatorQuery =
            "SELECT p.numero_personal, p.es_coordinador, u.* " +
            "FROM PROFESOR p JOIN USUARIO u ON p.usuario_id = u.id " +
            "WHERE p.es_coordinador = true";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectCoordinatorStatement = connection.prepareStatement(selectCoordinatorQuery);
             ResultSet resultSet = selectCoordinatorStatement.executeQuery()) {

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

        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException selectException) {
            throw new BusinessException("Error obteniendo coordinador", selectException);
        }
    }
}
