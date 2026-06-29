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

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
import mx.uv.internshipprogramsystem.logic.dto.NotificationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.INotificationDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class NotificationDAO implements INotificationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDAO.class);

    private static final String INSERT_QUERY =
        "INSERT INTO MENSAJE_BANDEJA (usuario_id, mensaje, fecha_creacion, leido) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
        
    private static final String SELECT_BY_USER_QUERY =
        "SELECT id, usuario_id, mensaje, fecha_creacion, leido FROM MENSAJE_BANDEJA WHERE usuario_id = ? ORDER BY fecha_creacion DESC";
        
    private static final String UPDATE_READ_STATUS_QUERY =
        "UPDATE MENSAJE_BANDEJA SET leido = TRUE WHERE id = ?";

    @Override
    public boolean insert(NotificationDTO notification) throws BusinessException {
        InputValidator.validateNotNull(notification, "La notificación no puede ser nula.");
        
        boolean wasInserted = false;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
             
            preparedStatement.setInt(1, notification.getUserId());
            preparedStatement.setString(2, notification.getMessage());
            preparedStatement.setBoolean(3, notification.getIsRead() != null ? notification.getIsRead() : false);
            
            wasInserted = preparedStatement.executeUpdate() > 0;
            
        } catch (SQLTransientConnectionException e) {
            LOGGER.error("Error de conexión a BD al insertar notificación", e);
            throw new BusinessException("No se pudo conectar con la base de datos.", e);
        } catch (SQLException e) {
            LOGGER.error("Error al insertar notificación", e);
            throw new BusinessException("Error al guardar la notificación en la base de datos.", e);
        }
        return wasInserted;
    }

    @Override
    public List<NotificationDTO> findByUserId(int userId) throws BusinessException {
        InputValidator.validatePositive(userId, "El ID del usuario debe ser positivo.");
        
        List<NotificationDTO> notifications = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_USER_QUERY)) {
             
            preparedStatement.setInt(1, userId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(new NotificationDTO(
                        resultSet.getInt("id"),
                        resultSet.getInt("usuario_id"),
                        resultSet.getString("mensaje"),
                        resultSet.getTimestamp("fecha_creacion").toLocalDateTime(),
                        resultSet.getBoolean("leido")
                    ));
                }
            }
            
        } catch (SQLTransientConnectionException e) {
            LOGGER.error("Error de conexión a BD al buscar notificaciones", e);
            throw new BusinessException("No se pudo conectar con la base de datos.", e);
        } catch (SQLException e) {
            LOGGER.error("Error al buscar notificaciones para usuario " + userId, e);
            throw new BusinessException("Error al consultar las notificaciones.", e);
        }
        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId) throws BusinessException {
        InputValidator.validatePositive(notificationId, "El ID de la notificación debe ser positivo.");
        
        boolean wasUpdated = false;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_READ_STATUS_QUERY)) {
             
            preparedStatement.setInt(1, notificationId);
            wasUpdated = preparedStatement.executeUpdate() > 0;
            
        } catch (SQLTransientConnectionException e) {
            LOGGER.error("Error de conexión a BD al actualizar notificación", e);
            throw new BusinessException("No se pudo conectar con la base de datos.", e);
        } catch (SQLException e) {
            LOGGER.error("Error al marcar notificación como leída", e);
            throw new BusinessException("Error al actualizar la notificación.", e);
        }
        return wasUpdated;
    }
}
