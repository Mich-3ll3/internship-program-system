package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectAssignmentDAO {

    boolean insert(ProjectAssignmentDTO assignment) throws BusinessException;

    Optional<ProjectAssignmentDTO> findById(int id) throws BusinessException;

    boolean delete(int id) throws BusinessException;
}
