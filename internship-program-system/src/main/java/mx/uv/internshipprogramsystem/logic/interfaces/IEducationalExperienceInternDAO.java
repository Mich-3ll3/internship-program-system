package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.List;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternStatus;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IEducationalExperienceInternDAO {
    boolean create(EducationalExperienceInternDTO assignment) throws BusinessException, DataAccessException;
    List<EducationalExperienceInternDTO> findByNrc(String nrc) throws BusinessException, DataAccessException;
    boolean existsAssignment(EducationalExperienceInternDTO assignment) throws BusinessException, DataAccessException;
    boolean existsActiveAssignmentByInternId(int internId) throws BusinessException, DataAccessException;
    boolean existsActiveEducationalExperienceByNrc(String nrc) throws BusinessException, DataAccessException;
    int countValidOpportunitiesByInternId(int internId) throws BusinessException, DataAccessException;
    boolean closeActiveEducationalExperience(
        int internId,
        EducationalExperienceInternStatus status
    ) throws BusinessException, DataAccessException;
}