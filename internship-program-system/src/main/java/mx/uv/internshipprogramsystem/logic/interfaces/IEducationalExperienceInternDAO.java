package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceInternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IEducationalExperienceInternDAO {
    boolean create(EducationalExperienceInternDTO assignment) throws BusinessException;
    boolean existsAssignment(EducationalExperienceInternDTO assignment) throws BusinessException;
    boolean existsActiveAssignmentByInternId(int internId) throws BusinessException;
    boolean existsActiveEducationalExperienceByNrc(String nrc) throws BusinessException;
    int countValidOpportunitiesByInternId(int internId) throws BusinessException;
}