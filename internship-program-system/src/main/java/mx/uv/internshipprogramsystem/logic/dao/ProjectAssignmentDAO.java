package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.interfaces.IProjectAssignment;
import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectAssignmentException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectAssignmentDAO implements IProjectAssignment {

    @Override
    public boolean insert(ProjectAssignmentDTO assignment) throws ProjectAssignmentException {
        boolean operationSuccessful = false;
        String insertAssignmentQuery = "INSERT INTO ASIGNACION_PROYECTO (estudiante_id, proyecto_id, fecha_asignacion) VALUES (?, ?, ?)";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement insertAssignmentStatement = Connection.prepareStatement(insertAssignmentQuery)) {

            insertAssignmentStatement.setInt(1, assignment.getStudentId());
            insertAssignmentStatement.setInt(2, assignment.getProjectId());
            insertAssignmentStatement.setString(3, assignment.getAssignmentDate());

            int affectedRows = insertAssignmentStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectAssignmentException("Error inserting project assignment", exception);
        }
        return operationSuccessful;
    }

    @Override
    public ProjectAssignmentDTO findById(int id) throws ProjectAssignmentException {
        ProjectAssignmentDTO assignmentResult = null;
        String selectAssignmentQuery = "SELECT id, estudiante_id, proyecto_id, fecha_asignacion FROM ASIGNACION_PROYECTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectAssignment = Connection.prepareStatement(selectAssignmentQuery)) {

            statementSelectAssignment.setInt(1, id);
            try (ResultSet resultSetAssignment = statementSelectAssignment.executeQuery()) {
                if (resultSetAssignment.next()) {
                    assignmentResult = new ProjectAssignmentDTO(
                        resultSetAssignment.getInt("id"),
                        resultSetAssignment.getInt("estudiante_id"),
                        resultSetAssignment.getInt("proyecto_id"),
                        resultSetAssignment.getString("fecha_asignacion")
                    );
                }
            }
        } catch (SQLException exception) {
            throw new ProjectAssignmentException("Error finding project assignment with id " + id, exception);
        }
        return assignmentResult;
    }

    @Override
    public boolean delete(int id) throws ProjectAssignmentException {
        boolean operationSuccessful = false;
        String deleteAssignmentById = "DELETE FROM ASIGNACION_PROYECTO WHERE id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement deleteAssignmentStatement = Connection.prepareStatement(deleteAssignmentById)) {

            deleteAssignmentStatement.setInt(1, id);
            int affectedRows = deleteAssignmentStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectAssignmentException("Error deleting project assignment with id " + id, exception);
        }
        return operationSuccessful;
    }
}
