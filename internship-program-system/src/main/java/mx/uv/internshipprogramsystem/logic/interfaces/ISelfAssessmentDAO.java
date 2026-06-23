package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface ISelfAssessmentDAO {
    boolean registerSelfAssessment(SelfAssessmentDTO selfAssessment) throws BusinessException, DataAccessException;
    List<SelfAssessmentDTO> getAllSelfAssessments() throws BusinessException, DataAccessException;
    SelfAssessmentDTO getSelfAssessmentByStudent(int studentId) throws BusinessException, DataAccessException;
}