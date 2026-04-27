package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectResponsibleDAO {

    boolean insert(ProjectResponsibleDTO responsible) throws BusinessException;

    ProjectResponsibleDTO findById(int id) throws BusinessException;

    boolean delete(int id) throws BusinessException;
}
