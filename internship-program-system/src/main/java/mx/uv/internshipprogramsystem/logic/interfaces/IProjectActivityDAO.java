package mx.uv.internshipprogramsystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;

import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectActivityDAO {

    boolean create(ProjectActivityDTO activity, Connection connection) throws BusinessException;
    List<ProjectActivityDTO> findByProjectId(Integer projectId) throws BusinessException;
    boolean deleteByProjectId(Integer projectId, Connection connection) throws BusinessException;
}