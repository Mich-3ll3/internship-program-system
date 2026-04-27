package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectResponsibleException;

public interface IProjectResponsibleDAO {
    boolean insert(ProjectResponsibleDTO responsible) throws ProjectResponsibleException;
    ProjectResponsibleDTO findById(int id) throws ProjectResponsibleException;
    boolean delete(int id) throws ProjectResponsibleException;
}
