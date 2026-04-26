package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectDAO {
    boolean createProject(ProjectDTO project) throws BusinessException;
    List<ProjectDTO> findAll() throws BusinessException;
    List<ProjectDTO> findByStatus(boolean isActive) throws BusinessException;
    boolean update(ProjectDTO project) throws BusinessException;
    boolean delete(int id) throws BusinessException;
}

