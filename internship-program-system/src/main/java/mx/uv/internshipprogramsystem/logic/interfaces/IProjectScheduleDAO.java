package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.List;

import mx.uv.internshipprogramsystem.logic.dto.ProjectScheduleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectScheduleDAO {

    boolean create(ProjectScheduleDTO schedule, Connection connection) throws BusinessException, DataAccessException;
    List<ProjectScheduleDTO> findByProjectId(Integer projectId) throws BusinessException, DataAccessException;
    boolean deleteByProjectId(Integer projectId, Connection connection) throws BusinessException, DataAccessException;
}