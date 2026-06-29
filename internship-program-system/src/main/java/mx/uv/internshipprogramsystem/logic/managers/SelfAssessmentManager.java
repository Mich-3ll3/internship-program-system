package mx.uv.internshipprogramsystem.logic.managers;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.time.LocalDate;
import java.util.List;

import mx.uv.internshipprogramsystem.logic.dao.SelfAssessmentDAO;
import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class SelfAssessmentManager {

    private static final String ERROR_GET_ASSESSMENTS = "Ocurrió un error al obtener las autoevaluaciones.";
    private static final String ERROR_REGISTER_ASSESSMENT = "No se pudo registrar la autoevaluación.";
    private static final String ERROR_NULL_STUDENT = "Debes seleccionar un estudiante.";
    private static final String ERROR_NULL_PROJECT = "Debes seleccionar un proyecto.";
    private static final String ERROR_NULL_ORG = "Debes seleccionar una organización.";
    private static final String ERROR_NULL_RESPONSIBLE = "Debes seleccionar un responsable.";
    private static final String ERROR_BLANK_DEPT = "El departamento no puede estar vacío.";
    private static final String ERROR_BLANK_PLACE = "El lugar no puede estar vacío.";

    private final SelfAssessmentDAO selfAssessmentDAO;

    public SelfAssessmentManager() {
        this.selfAssessmentDAO = new SelfAssessmentDAO();
    }

    public List<SelfAssessmentDTO> getAllSelfAssessments() throws BusinessException, DataAccessException {
        try {
            return selfAssessmentDAO.getAllSelfAssessments();
        } catch (Exception exception) {
            throw new BusinessException(ERROR_GET_ASSESSMENTS, exception);
        }
    }

    public List<SelfAssessmentDTO> getSelfAssessmentsByStudentId(int studentId) throws BusinessException {
        try {
            return selfAssessmentDAO.getSelfAssessmentsByStudentId(studentId);
        } catch (Exception exception) {
            throw new BusinessException(ERROR_GET_ASSESSMENTS, exception);
        }
    }

    public void registerSelfAssessment(
            Integer studentId,
            Integer projectId,
            Integer organizationId,
            Integer responsibleId,
            String department,
            String place,
            LocalDate date,
            int afirmacion1,
            int afirmacion2,
            int afirmacion3,
            int afirmacion4,
            int afirmacion5,
            int afirmacion6,
            int afirmacion7,
            int afirmacion8,
            int afirmacion9,
            int afirmacion10,
            String observations
    ) throws BusinessException, DataAccessException {

        validateData(studentId, projectId, organizationId, responsibleId, department, place);

        if (date == null) {
            date = LocalDate.now();
        }

        SelfAssessmentDTO dto = new SelfAssessmentDTO();
        dto.setStudentId(studentId);
        dto.setProjectId(projectId);
        dto.setOrganizationId(organizationId);
        dto.setResponsibleId(responsibleId);

        dto.setDepartment(department);
        dto.setPlace(place);
        dto.setDate(date);

        dto.setAfirmacion1(afirmacion1);
        dto.setAfirmacion2(afirmacion2);
        dto.setAfirmacion3(afirmacion3);
        dto.setAfirmacion4(afirmacion4);
        dto.setAfirmacion5(afirmacion5);
        dto.setAfirmacion6(afirmacion6);
        dto.setAfirmacion7(afirmacion7);
        dto.setAfirmacion8(afirmacion8);
        dto.setAfirmacion9(afirmacion9);
        dto.setAfirmacion10(afirmacion10);

        dto.setObservations(observations);

        try {
            selfAssessmentDAO.insert(dto);
        } catch (Exception exception) {
            throw new BusinessException(ERROR_REGISTER_ASSESSMENT, exception);
        }
    }

    private void validateData(
            Integer studentId,
            Integer projectId,
            Integer organizationId,
            Integer responsibleId,
            String department,
            String place
    ) throws BusinessException, DataAccessException {

        if (studentId == null)
            throw new BusinessException("Debes seleccionar un estudiante.");

        if (projectId == null)
            throw new BusinessException("Debes seleccionar un proyecto.");

        if (organizationId == null)
            throw new BusinessException("Debes seleccionar una organización.");

        if (responsibleId == null)
            throw new BusinessException("Debes seleccionar un responsable.");

        if (department == null || department.isBlank())
            throw new BusinessException("El departamento no puede estar vacío.");

        if (place == null || place.isBlank())
            throw new BusinessException("El lugar no puede estar vacío.");
    }
}