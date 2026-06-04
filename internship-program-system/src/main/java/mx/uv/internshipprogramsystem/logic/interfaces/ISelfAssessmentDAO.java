package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface ISelfAssessmentDAO {
    boolean registerSelfAssessment(SelfAssessmentDTO selfAssessment) throws BusinessException;
    List<SelfAssessmentDTO> getAllSelfAssessments() throws BusinessException;
    SelfAssessmentDTO getSelfAssessmentByStudent(int studentId) throws BusinessException;
}
