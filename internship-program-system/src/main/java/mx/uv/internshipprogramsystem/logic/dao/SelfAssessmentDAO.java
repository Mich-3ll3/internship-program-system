package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
import mx.uv.internshipprogramsystem.logic.dto.SelfAssessmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelfAssessmentDAO {

    private static final String GET_ALL =
        "SELECT " +
        "a.id, a.estudiante_id, a.proyecto_id, a.organizacion_id, a.responsable_id, " +
        "a.fecha, a.departamento, a.lugar, a.observaciones, " +
        "a.afirmacion1, a.afirmacion2, a.afirmacion3, a.afirmacion4, a.afirmacion5, " +
        "a.afirmacion6, a.afirmacion7, a.afirmacion8, a.afirmacion9, a.afirmacion10, " +
        "CONCAT(u.nombre, ' ', u.apellido_paterno, ' ', u.apellido_materno) AS studentName, " +
        "p.nombre AS projectName, " +
        "CONCAT(r.nombre, ' ', r.apellido_paterno, ' ', r.apellido_materno) AS responsibleName, " +
        "o.nombre AS organizationName " +
        "FROM AUTOEVALUACION a " +
        "JOIN ESTUDIANTE e ON a.estudiante_id = e.usuario_id " +
        "JOIN USUARIO u ON e.usuario_id = u.id " +
        "JOIN PROYECTO p ON a.proyecto_id = p.id " +
        "JOIN RESPONSABLE_PROYECTO r ON a.responsable_id = r.id " +
        "JOIN ORGANIZACION_VINCULADA o ON a.organizacion_id = o.id";

    private static final String GET_BY_STUDENT_ID = GET_ALL + " WHERE a.estudiante_id = ?";

    private static final String INSERT =
        "INSERT INTO AUTOEVALUACION (" +
        "estudiante_id, proyecto_id, organizacion_id, responsable_id, " +
        "departamento, lugar, fecha, " +
        "afirmacion1, afirmacion2, afirmacion3, afirmacion4, afirmacion5, " +
        "afirmacion6, afirmacion7, afirmacion8, afirmacion9, afirmacion10, " +
        "observaciones) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public List<SelfAssessmentDTO> getAllSelfAssessments() throws BusinessException, DataAccessException {
    List<SelfAssessmentDTO> assessments = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                assessments.add(mapResultSetToDTO(resultSet));
            }

        } catch (SQLException sqlException) {
            throw new BusinessException("Error al obtener autoevaluaciones: " + sqlException.getMessage());
        }

        return assessments;
    }

    public List<SelfAssessmentDTO> getSelfAssessmentsByStudentId(int studentId) throws BusinessException {
        List<SelfAssessmentDTO> assessments = new ArrayList<>();
        
        final int PARAM_STUDENT_ID = 1;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_STUDENT_ID)) {
             
            preparedStatement.setInt(PARAM_STUDENT_ID, studentId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    assessments.add(mapResultSetToDTO(resultSet));
                }
            }

        } catch (SQLException sqlException) {
            throw new BusinessException("Error al obtener autoevaluaciones del estudiante: " + sqlException.getMessage());
        }

        return assessments;
    }

    public void insert(SelfAssessmentDTO dto) throws BusinessException, DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT)) {

            preparedStatement.setInt(1, dto.getStudentId());
            preparedStatement.setInt(2, dto.getProjectId());
            preparedStatement.setInt(3, dto.getOrganizationId());
            preparedStatement.setInt(4, dto.getResponsibleId());

            preparedStatement.setString(5, dto.getDepartment());
            preparedStatement.setString(6, dto.getPlace());
            preparedStatement.setDate(7, java.sql.Date.valueOf(dto.getDate()));

            preparedStatement.setInt(8, dto.getAfirmacion1());
            preparedStatement.setInt(9, dto.getAfirmacion2());
            preparedStatement.setInt(10, dto.getAfirmacion3());
            preparedStatement.setInt(11, dto.getAfirmacion4());
            preparedStatement.setInt(12, dto.getAfirmacion5());
            preparedStatement.setInt(13, dto.getAfirmacion6());
            preparedStatement.setInt(14, dto.getAfirmacion7());
            preparedStatement.setInt(15, dto.getAfirmacion8());
            preparedStatement.setInt(16, dto.getAfirmacion9());
            preparedStatement.setInt(17, dto.getAfirmacion10());

            preparedStatement.setString(18, dto.getObservations());

            preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            throw new BusinessException("Error al registrar autoevaluación: " + sqlException.getMessage());
        }
    }
}