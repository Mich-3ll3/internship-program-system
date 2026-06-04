package mx.uv.internshipprogramsystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;

import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectScheduleDAO {

    boolean create(ProjectScheduleDTO schedule, Connection connection) throws BusinessException;
    List<ProjectScheduleDTO> findByProjectId(Integer projectId) throws BusinessException;
    boolean deleteByProjectId(Integer projectId, Connection connection) throws BusinessException;
}