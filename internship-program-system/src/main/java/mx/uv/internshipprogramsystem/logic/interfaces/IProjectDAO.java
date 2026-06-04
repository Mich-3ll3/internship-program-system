package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectDAO {
    boolean create(ProjectDTO project)
            throws BusinessException;

    boolean update(ProjectDTO project)
            throws BusinessException;

    boolean deactivate(int id)
            throws BusinessException;

    Optional<ProjectDTO> findById(int id)
            throws BusinessException;

    List<ProjectDTO> findAll()
            throws BusinessException;

    List<ProjectDTO> findByStatus(boolean isActive)
            throws BusinessException;

    int countAll()
            throws BusinessException;
}