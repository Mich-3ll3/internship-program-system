package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IInternDAO;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IInternDAO;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;

public class InternDAO implements IInternDAO{
    private static final Logger logger = LoggerFactory.getLogger(InternDAO.class);

    public InternDAO() {
    }

    public boolean create(InternDTO intern) throws BusinessException {
        InternValidator validator = new InternValidator();
        validator.validateEnrollmentNumber(intern.getEnrollmentNumber());
        
        String insertInternQuery = "INSERT INTO ESTUDIANTE (matricula, id_usuario) VALUES (?,?)";
        
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertInternStatement = connection.prepareStatement(insertInternQuery)){
            
            insertInternStatement.setString(1, intern.getEnrollmentNumber());
            insertInternStatement.setInt(2,intern.getId());
            
            return insertInternStatement.executeUpdate() > 0;
            
        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            throw new BusinessException("Error creando al estudiante.", sqlException);
        }
    }

    public InternDTO findByMatricula(String enrollmentNumber) throws BusinessException {
        String selectInternQuery = "SELECT * FROM ESTUDIANTE WHERE matricula = ?";
        
        InternDTO intern = null;
        
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectInternStatement = connection.prepareStatement(selectInternQuery)) {

            selectInternStatement.setString(1, enrollmentNumber);
            
            try (ResultSet resultSet = selectInternStatement.executeQuery()) {
                if (resultSet.next()) {
                    intern = new InternDTO();
                    intern.setEnrollmentNumber(resultSet.getString("matricula"));
                    intern.setId(resultSet.getInt("id_usuario"));
                }
                return intern;
            }

        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            throw new BusinessException("Error buscando estudiante con matrícula " + enrollmentNumber, sqlException);
        }
    }

    public List<InternDTO> findAll() throws BusinessException {
        String selectAllInternsQuery = "SELECT * FROM ESTUDIANTE";
        List<InternDTO> interns = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectAllInternsStatement = connection.prepareStatement(selectAllInternsQuery);
             ResultSet resultSet = selectAllInternsStatement.executeQuery()) {

            while (resultSet.next()) {
                InternDTO intern = new InternDTO();
                intern.setEnrollmentNumber(resultSet.getString("matricula"));
                intern.setId(resultSet.getInt("id_usuario"));
                interns.add(intern);
            }
            return interns;

        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            throw new BusinessException("Error obteniendo la lista de estudiantes", sqlException);
        }
    }

    public boolean update(InternDTO intern) throws BusinessException {
        String updateInternQuery = "UPDATE ESTUDIANTE SET id_usuario = ? WHERE matricula = ?";
        
        InternValidator validator = new InternValidator();
        validator.validateEnrollmentNumber(intern.getEnrollmentNumber());
        
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateInternStatement = connection.prepareStatement(updateInternQuery)) {

            updateInternStatement.setInt(1, intern.getId());
            updateInternStatement.setString(2, intern.getEnrollmentNumber());
            
            return updateInternStatement.executeUpdate() > 0;

        } catch (SQLTransientConnectionException connectionException) {
            throw new BusinessException("No se pudo conectar con la base de datos.", connectionException);
        } catch (SQLException sqlException) {
            throw new BusinessException("Error actualizando estudiante con matrícula " + intern.getEnrollmentNumber(), sqlException);
        }
    }
}
