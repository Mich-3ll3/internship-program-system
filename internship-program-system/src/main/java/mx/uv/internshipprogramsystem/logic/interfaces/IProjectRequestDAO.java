package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import java.util.List;

public interface IProjectRequestDAO {

    boolean insert(ProjectRequestDTO request) throws BusinessException;

    List<ProjectRequestDTO> findByStudent(int studentId) throws BusinessException;

    boolean delete(ProjectRequestDTO request) throws BusinessException;
}
