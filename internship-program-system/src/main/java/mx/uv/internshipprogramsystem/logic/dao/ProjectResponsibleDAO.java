package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectResponsibleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectResponsibleDAO implements IProjectResponsibleDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectResponsibleDAO.class);

    @Override
    public boolean insert(ProjectResponsibleDTO responsible) throws BusinessException {
        String insertResponsibleQuery = "INSERT INTO RESPONSABLE_PROYECTO (nombre, apellido_paterno, apellido_materno, correo, cargo, organizacion_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertResponsibleStatement = connection.prepareStatement(insertResponsibleQuery)) {

            insertResponsibleStatement.setString(1, responsible.getFirstName());
            insertResponsibleStatement.setString(2, responsible.getLastNameFather());
            insertResponsibleStatement.setString(3, responsible.getLastNameMother());
            insertResponsibleStatement.setString(4, responsible.getEmail());
            insertResponsibleStatement.setString(5, responsible.getPosition());
            insertResponsibleStatement.setInt(6, responsible.getOrganizationId());

            int affectedRows = insertResponsibleStatement.executeUpdate();
            logger.info("Responsable de proyecto {} insertado correctamente", responsible.getEmail());
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            logger.error("Error insertando responsable de proyecto {}", responsible.getEmail(), sqlException);
            throw new BusinessException("Error insertando responsable de proyecto " + responsible.getEmail(), sqlException);
        }
    }

    @Override
    public ProjectResponsibleDTO findById(int id) throws BusinessException {
        String selectResponsibleQuery = "SELECT id, nombre, apellido_paterno, apellido_materno, correo, cargo, organizacion_id FROM RESPONSABLE_PROYECTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectResponsible = connection.prepareStatement(selectResponsibleQuery)) {

            statementSelectResponsible.setInt(1, id);
            try (ResultSet resultSetResponsible = statementSelectResponsible.executeQuery()) {
                if (resultSetResponsible.next()) {
                    ProjectResponsibleDTO responsibleResult = new ProjectResponsibleDTO(
                        resultSetResponsible.getInt("id"),
                        resultSetResponsible.getString("nombre"),
                        resultSetResponsible.getString("apellido_paterno"),
                        resultSetResponsible.getString("apellido_materno"),
                        resultSetResponsible.getString("correo"),
                        resultSetResponsible.getString("cargo"),
                        resultSetResponsible.getInt("organizacion_id")
                    );
                    logger.info("Responsable de proyecto con id {} encontrado", id);
                    return responsibleResult;
                } else {
                    logger.warn("No se encontró responsable de proyecto con id {}", id);
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            logger.error("Error buscando responsable de proyecto con id {}", id, sqlException);
            throw new BusinessException("Error buscando responsable de proyecto con id " + id, sqlException);
        }
    }

    @Override
    public boolean delete(int id) throws BusinessException {
        String deleteResponsibleById = "DELETE FROM RESPONSABLE_PROYECTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteResponsibleStatement = connection.prepareStatement(deleteResponsibleById)) {

            deleteResponsibleStatement.setInt(1, id);
            int affectedRows = deleteResponsibleStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Responsable de proyecto con id {} eliminado correctamente", id);
                return true;
            } else {
                logger.warn("No se eliminó responsable de proyecto con id {}", id);
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error eliminando responsable de proyecto con id {}", id, sqlException);
            throw new BusinessException("Error eliminando responsable de proyecto con id " + id, sqlException);
        }
    }
}
