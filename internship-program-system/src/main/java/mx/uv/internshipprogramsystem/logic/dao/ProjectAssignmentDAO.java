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
import java.sql.Date;

public class ProjectAssignmentDAO implements IProjectAssignmentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectAssignmentDAO.class);

    @Override
    public boolean insert(ProjectAssignmentDTO assignment) throws BusinessException {
        String insertAssignmentQuery =
            "INSERT INTO asignacion_proyecto (estudiante_id, proyecto_id, profesor_id, fecha_asignacion) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertAssignmentStatement = connection.prepareStatement(insertAssignmentQuery)) {

            insertAssignmentStatement.setInt(1, assignment.getStudentId());
            insertAssignmentStatement.setInt(2, assignment.getProjectId());
            insertAssignmentStatement.setInt(3, assignment.getProfessorId());
            insertAssignmentStatement.setDate(4, Date.valueOf(assignment.getAssignmentDate()));

            // 👇 Log para verificar los valores que llegan
            logger.debug("Valores recibidos → estudiante: {}, proyecto: {}, profesor: {}, fecha: {}",
                assignment.getStudentId(), assignment.getProjectId(),
                assignment.getProfessorId(), assignment.getAssignmentDate());

            int affectedRows = insertAssignmentStatement.executeUpdate();
            logger.info("Asignación insertada: estudiante {} → proyecto {} con profesor {} en fecha {}",
                        assignment.getStudentId(), assignment.getProjectId(),
                        assignment.getProfessorId(), assignment.getAssignmentDate());
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            logger.error("Error insertando asignación estudiante {} → proyecto {} con profesor {}",
                         assignment.getStudentId(), assignment.getProjectId(),
                         assignment.getProfessorId(), sqlException);
            throw new BusinessException("Error insertando asignación de proyecto", sqlException);
        }
    }

    @Override
    public ProjectAssignmentDTO findById(int estudianteId) throws BusinessException {
        String selectAssignmentQuery =
            "SELECT estudiante_id, proyecto_id, profesor_id, fecha_asignacion FROM asignacion_proyecto WHERE estudiante_id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectAssignment = connection.prepareStatement(selectAssignmentQuery)) {

            statementSelectAssignment.setInt(1, estudianteId);
            try (ResultSet resultSetAssignment = statementSelectAssignment.executeQuery()) {
                if (resultSetAssignment.next()) {
                    ProjectAssignmentDTO assignmentResult = new ProjectAssignmentDTO(
                        resultSetAssignment.getInt("estudiante_id"),
                        resultSetAssignment.getInt("proyecto_id"),
                        resultSetAssignment.getInt("profesor_id"),
                        resultSetAssignment.getDate("fecha_asignacion").toLocalDate()
                    );
                    logger.info("Asignación encontrada para estudiante {}", estudianteId);
                    return assignmentResult;
                } else {
                    logger.warn("No se encontró asignación para estudiante {}", estudianteId);
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            logger.error("Error buscando asignación para estudiante {}", estudianteId, sqlException);
            throw new BusinessException("Error buscando asignación para estudiante " + estudianteId, sqlException);
        }
    }

    @Override
    public boolean delete(int estudianteId) throws BusinessException {
        String deleteAssignmentById = "DELETE FROM asignacion_proyecto WHERE estudiante_id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteAssignmentStatement = connection.prepareStatement(deleteAssignmentById)) {

            deleteAssignmentStatement.setInt(1, estudianteId);
            int affectedRows = deleteAssignmentStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Asignación del estudiante {} eliminada correctamente", estudianteId);
                return true;
            } else {
                logger.warn("No se eliminó asignación del estudiante {}", estudianteId);
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error eliminando asignación del estudiante {}", estudianteId, sqlException);
            throw new BusinessException("Error eliminando asignación del estudiante " + estudianteId, sqlException);
        }
    }
}
