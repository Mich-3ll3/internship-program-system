package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectDAO {
    boolean create(ProjectDTO project)
            throws BusinessException, DataAccessException;

    boolean update(ProjectDTO project)
            throws BusinessException, DataAccessException;
    
    boolean update(
            ProjectDTO project,
            Connection connection
    ) throws BusinessException, DataAccessException;

    boolean deactivate(int id)
            throws BusinessException, DataAccessException;

    Optional<ProjectDTO> findById(int id)
            throws BusinessException, DataAccessException;

    List<ProjectDTO> findAll()
            throws BusinessException, DataAccessException;

    List<ProjectDTO> findByStatus(boolean isActive)
            throws BusinessException, DataAccessException;

    int countAll()
            throws BusinessException, DataAccessException;
    int createAndReturnId(
            ProjectDTO project,
            Connection connection
    ) throws BusinessException, DataAccessException;
}