package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectResponsibleDAO {
    boolean insert(
            ProjectResponsibleDTO responsible
    ) throws BusinessException, DataAccessException;

    Optional<ProjectResponsibleDTO> findById(
            int id
    ) throws BusinessException, DataAccessException;

    List<ProjectResponsibleDTO> findAll()
            throws BusinessException, DataAccessException;

    List<ProjectResponsibleDTO> findBySearchText(
            String searchText
    ) throws BusinessException, DataAccessException;

    boolean delete(
            int id
    ) throws BusinessException, DataAccessException;
}