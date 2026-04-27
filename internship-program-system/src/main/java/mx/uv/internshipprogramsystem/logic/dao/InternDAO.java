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
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IInternDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

public class InternDAO implements IInternDAO{
    private static final Logger LOGGER = LoggerFactory.getLogger(InternDAO.class);

    public InternDAO() {
    }

    public boolean create(InternDTO intern) throws BusinessException {
        InputValidator.validateNotNull(intern, "InternDTO no puede ser nulo.");
        InternValidator validator = new InternValidator();
        validator.validateEnrollmentNumber(intern.getEnrollmentNumber());
        
        String insertInternQuery = "INSERT INTO ESTUDIANTE (matricula, usuario_id) VALUES (?,?)";
        
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertInternStatement = connection.prepareStatement(insertInternQuery)){
            
            insertInternStatement.setString(1, intern.getEnrollmentNumber());
            insertInternStatement.setInt(2,intern.getId());
            
            return insertInternStatement.executeUpdate() > 0;
            
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo establecer conexión con la base de datos.", connectionException);
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad: matrícula duplicada", integrityException);
            throw new BusinessException("La matrícula ya existe.", integrityException);
        } catch (SQLException insertException) {
            LOGGER.error("Error SQL al insertar estudiante", insertException);
            throw new BusinessException("Error al insertar el estudiante en la base de datos.", insertException);
        }
    }
    
    public boolean update(InternDTO intern) throws BusinessException {
        InputValidator.validateNotNull(intern, "InternDTO no puede ser nulo.");
        InternValidator validator = new InternValidator();
        validator.validateEnrollmentNumber(intern.getEnrollmentNumber());
        
        String updateInternQuery = "UPDATE ESTUDIANTE SET usuario_id = ? WHERE matricula = ?";
        
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateInternStatement = connection.prepareStatement(updateInternQuery)) {

            updateInternStatement.setInt(1, intern.getId());
            updateInternStatement.setString(2, intern.getEnrollmentNumber());
            
            return updateInternStatement.executeUpdate() > 0;

        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLIntegrityConstraintViolationException integrityException) {
            LOGGER.error("Violación de integridad al actualizar estudiante", integrityException);
            throw new BusinessException("El usuario asociado no existe o la matrícula es inválida.", integrityException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al actualizar estudiante con matrícula {}", intern.getEnrollmentNumber(), sqlException);
            throw new BusinessException("Error actualizando estudiante con matrícula " + intern.getEnrollmentNumber(), sqlException);
        }
    }

    public InternDTO findByMatricula(String enrollmentNumber) throws BusinessException {
        InputValidator.validateNotEmpty(enrollmentNumber, "La matrícula no puede estar vacía.");

        String selectInternQuery =
            "SELECT e.matricula, u.* " +
            "FROM ESTUDIANTE e JOIN USUARIO u ON e.usuario_id = u.id " +
            "WHERE e.matricula = ?";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectInternStatement = connection.prepareStatement(selectInternQuery)) {

            selectInternStatement.setString(1, enrollmentNumber);

            try (ResultSet resultSet = selectInternStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new InternDTO(
                        resultSet.getString("matricula"),
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
            throw new BusinessException("Error buscando estudiante con matrícula " + enrollmentNumber, selectException);
        }
    }

    public List<InternDTO> findAll() throws BusinessException {
        String selectAllInternsQuery =
            "SELECT u.nombre " +
            "FROM ESTUDIANTE e JOIN USUARIO u ON e.usuario_id = u.id";
        List<InternDTO> interns = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllInternsStatement = connection.prepareStatement(selectAllInternsQuery);
             ResultSet resultSet = selectAllInternsStatement.executeQuery()) {

            while (resultSet.next()) {
                InternDTO intern = new InternDTO();
                intern.setName(resultSet.getString("nombre")); // solo asigna el nombre
                interns.add(intern);
            }
            return interns;

        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al obtener la lista de estudiantes", sqlException);
            throw new BusinessException("Error obteniendo la lista de estudiantes", sqlException);
        }
    }
}
