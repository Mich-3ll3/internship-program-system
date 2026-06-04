package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectResponsibleDAO {
    boolean insert(
            ProjectResponsibleDTO responsible
    ) throws BusinessException;

    Optional<ProjectResponsibleDTO> findById(
            int id
    ) throws BusinessException;

    List<ProjectResponsibleDTO> findAll()
            throws BusinessException;

    List<ProjectResponsibleDTO> findBySearchText(
            String searchText
    ) throws BusinessException;

    boolean delete(
            int id
    ) throws BusinessException;
}