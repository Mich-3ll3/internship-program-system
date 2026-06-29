package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.ProjectApplicationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProjectApplicationDAO {
    
    boolean createApplication(ProjectApplicationDTO application) throws BusinessException;
    
    List<ProjectApplicationDTO> getApplicationsByStudent(int internId) throws BusinessException;
    
    boolean deleteApplication(int internId, int projectId) throws BusinessException;
}