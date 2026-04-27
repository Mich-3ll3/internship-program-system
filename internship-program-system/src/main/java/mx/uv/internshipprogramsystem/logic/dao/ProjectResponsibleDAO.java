package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.interfaces.IProjectResponsible;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectResponsibleException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectResponsibleDAO implements IProjectResponsible {

    @Override
    public boolean insert(ProjectResponsibleDTO responsible) throws ProjectResponsibleException {
        boolean operationSuccessful = false;
        String insertResponsibleQuery = "INSERT INTO RESPONSABLE_PROYECTO (nombre, apellido_paterno, apellido_materno, correo, cargo, organizacion_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement insertResponsibleStatement = Connection.prepareStatement(insertResponsibleQuery)) {
            
            insertResponsibleStatement.setString(1, responsible.getFirstName());
            insertResponsibleStatement.setString(2, responsible.getLastNameFather());
            insertResponsibleStatement.setString(3, responsible.getLastNameMother());
            insertResponsibleStatement.setString(4, responsible.getEmail());
            insertResponsibleStatement.setString(5, responsible.getPosition());
            insertResponsibleStatement.setInt(6, responsible.getOrganizationId());

            int affectedRows = insertResponsibleStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectResponsibleException("Error inserting project responsible", exception);
        }
        return operationSuccessful;
    }

    @Override
    public ProjectResponsibleDTO findById(int id) throws ProjectResponsibleException {
        ProjectResponsibleDTO responsibleResult = null;
        String selectResponsibleQuery = "SELECT id, nombre, apellido_paterno, apellido_materno, correo, cargo, organizacion_id FROM RESPONSABLE_PROYECTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectResponsible = Connection.prepareStatement(selectResponsibleQuery)) {

            statementSelectResponsible.setInt(1, id);
            try (ResultSet resultSetResponsible = statementSelectResponsible.executeQuery()) {
                if (resultSetResponsible.next()) {
                    responsibleResult = new ProjectResponsibleDTO(
                        resultSetResponsible.getInt("id"),
                        resultSetResponsible.getString("nombre"),
                        resultSetResponsible.getString("apellido_paterno"),
                        resultSetResponsible.getString("apellido_materno"),
                        resultSetResponsible.getString("correo"),
                        resultSetResponsible.getString("cargo"),
                        resultSetResponsible.getInt("organizacion_id")
                    );
                }
            }
        } catch (SQLException exception) {
            throw new ProjectResponsibleException("Error finding project responsible with id " + id, exception);
        }
        return responsibleResult;
    }

    @Override
    public boolean delete(int id) throws ProjectResponsibleException {
        boolean operationSuccessful = false;
        String deleteResponsibleById = "DELETE FROM RESPONSABLE_PROYECTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement deleteResponsibleStatement = Connection.prepareStatement(deleteResponsibleById)) {

            deleteResponsibleStatement.setInt(1, id);
            int affectedRows = deleteResponsibleStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectResponsibleException("Error deleting project responsible with id " + id, exception);
        }
        return operationSuccessful;
    }
}
