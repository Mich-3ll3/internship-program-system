package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IUserDAO;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class UserDAO implements IUserDAO{
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    
    public UserDAO() {
    }
    
    public boolean create(UserDTO user, String plainPassword) throws BusinessException {
        String insertUserQuery = "INSERT INTO USUARIO (correo_institucional, contrasena, nombre, "
                               + "apellido_paterno, apellido_materno, activo, rol) "
                               + "VALUES (?,SHA(?,256),?,?,?,?,?)";
        
        UserValidator validator = new UserValidator();
        validator.validateUserForCreation(user, plainPassword);
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {

            insertUserStatement.setString(1, user.getInstitucionalEmail());
            insertUserStatement.setString(2, plainPassword);
            insertUserStatement.setString(3, user.getName());
            insertUserStatement.setString(4, user.getFirstSurname());
            insertUserStatement.setString(5, user.getSecondSurname());
            insertUserStatement.setBoolean(6, user.getIsActive());
            insertUserStatement.setString(7, user.getRol().name());

            return insertUserStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error creando el usuario " + user.getInstitucionalEmail(), sqlException);
        }
    }

    public boolean update(UserDTO user) throws BusinessException {
        String updateUserQuery = "UPDATE USUARIO SET nombre=?, apellido_paterno=?, apellido_materno=?, "
                               + "activo=?, rol=? WHERE correo_institucional=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery)) {
            updateUserStatement.setString(1, user.getName());
            updateUserStatement.setString(2, user.getFirstSurname());
            updateUserStatement.setString(3, user.getSecondSurname());
            updateUserStatement.setBoolean(4, user.getIsActive());
            updateUserStatement.setString(5, user.getRol().name());
            updateUserStatement.setString(6, user.getInstitucionalEmail());

            return updateUserStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error actualizando usuario " + user.getInstitucionalEmail(), sqlException);
        }
    }
    
    public boolean changeStatus(String email, boolean isActive) throws BusinessException {
        String changeUserStatusQuery = "UPDATE USUARIO SET activo=? WHERE correo_institucional=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement changeUserStatusStatement = connection.prepareStatement(changeUserStatusQuery)) {
            changeUserStatusStatement.setBoolean(1, isActive);
            changeUserStatusStatement.setString(2, email);
            return changeUserStatusStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error cambiando estado del usuario " + email, sqlException);
        }
    }

    public boolean login(String email, String plainPassword) throws BusinessException {
        String loginUserQuery = "SELECT COUNT(*) FROM USUARIO WHERE correo_institucional=? AND contrasena=SHA2(?,256)";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement loginUserStatement = connection.prepareStatement(loginUserQuery)) {
            loginUserStatement.setString(1, email);
            loginUserStatement.setString(2, plainPassword);

            try (ResultSet resultSet = loginUserStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException sqlException) {
            throw new BusinessException("Error verificando login para " + email, sqlException);
        }
    }

}
