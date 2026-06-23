package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.List;

import mx.uv.internshipprogramsystem.logic.dto.ProjectActivityDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectActivityDAO {

    boolean create(ProjectActivityDTO activity, Connection connection) throws BusinessException, DataAccessException;
    boolean create(ProjectActivityDTO activity) throws BusinessException, DataAccessException;
    List<ProjectActivityDTO> findByProjectId(Integer projectId) throws BusinessException, DataAccessException;
    boolean deleteByProjectId(Integer projectId, Connection connection) throws BusinessException, DataAccessException;
    boolean update(ProjectActivityDTO activity) throws BusinessException, DataAccessException;
    boolean delete(int id) throws BusinessException, DataAccessException;
    List<ProjectActivityDTO> findAll() throws BusinessException, DataAccessException;
    boolean saveAll(Integer projectId, List<ProjectActivityDTO> activities) throws BusinessException, DataAccessException;
}