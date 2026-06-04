package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IEducationalExperienceDAO {
    boolean create(EducationalExperienceDTO educationalExperience) throws BusinessException;
    Optional<EducationalExperienceDTO> findByNrc(String nrc) throws BusinessException;
    List<EducationalExperienceDTO> findAll() throws BusinessException;
}