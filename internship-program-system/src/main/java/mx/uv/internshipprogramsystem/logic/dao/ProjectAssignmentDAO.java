package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectAssignmentDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectAssignmentDAO implements IProjectAssignmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectAssignmentDAO.class);

    @Override
    public boolean insert(ProjectAssignmentDTO assignment) throws BusinessException {
        String insertAssignmentQuery = "INSERT INTO ASIGNACION_PROYECTO (estudiante_id, proyecto_id, fecha_asignacion) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertAssignmentStatement = connection.prepareStatement(insertAssignmentQuery)) {

            insertAssignmentStatement.setInt(1, assignment.getStudentId());
            insertAssignmentStatement.setInt(2, assignment.getProjectId());
            insertAssignmentStatement.setString(3, assignment.getAssignmentDate());

            int affectedRows = insertAssignmentStatement.executeUpdate();
            logger.info("Asignación insertada: estudiante {} → proyecto {}", assignment.getStudentId(), assignment.getProjectId());
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            logger.error("Error insertando asignación estudiante {} → proyecto {}", assignment.getStudentId(), assignment.getProjectId(), sqlException);
            throw new BusinessException("Error insertando asignación de proyecto", sqlException);
        }
    }

    @Override
    public ProjectAssignmentDTO findById(int id) throws BusinessException {
        String selectAssignmentQuery = "SELECT id, estudiante_id, proyecto_id, fecha_asignacion FROM ASIGNACION_PROYECTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectAssignment = connection.prepareStatement(selectAssignmentQuery)) {

            statementSelectAssignment.setInt(1, id);
            try (ResultSet resultSetAssignment = statementSelectAssignment.executeQuery()) {
                if (resultSetAssignment.next()) {
                    ProjectAssignmentDTO assignmentResult = new ProjectAssignmentDTO(
                        resultSetAssignment.getInt("id"),
                        resultSetAssignment.getInt("estudiante_id"),
                        resultSetAssignment.getInt("proyecto_id"),
                        resultSetAssignment.getString("fecha_asignacion")
                    );
                    logger.info("Asignación encontrada con id {}", id);
                    return assignmentResult;
                } else {
                    logger.warn("No se encontró asignación con id {}", id);
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            logger.error("Error buscando asignación con id {}", id, sqlException);
            throw new BusinessException("Error buscando asignación con id " + id, sqlException);
        }
    }

    @Override
    public boolean delete(int id) throws BusinessException {
        String deleteAssignmentById = "DELETE FROM ASIGNACION_PROYECTO WHERE id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteAssignmentStatement = connection.prepareStatement(deleteAssignmentById)) {

            deleteAssignmentStatement.setInt(1, id);
            int affectedRows = deleteAssignmentStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Asignación con id {} eliminada correctamente", id);
                return true;
            } else {
                logger.warn("No se eliminó asignación con id {}", id);
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error eliminando asignación con id {}", id, sqlException);
            throw new BusinessException("Error eliminando asignación con id " + id, sqlException);
        }
    }
}
