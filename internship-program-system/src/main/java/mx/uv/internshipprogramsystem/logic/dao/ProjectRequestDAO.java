package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectRequestDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectRequestDAO implements IProjectRequestDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRequestDAO.class);

    @Override
    public boolean insert(ProjectRequestDTO request) throws BusinessException {
        String insertRequestQuery = "INSERT INTO SOLICITUD_PROYECTO (estudiante_id, proyecto_id, prioridad) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertRequestStatement = connection.prepareStatement(insertRequestQuery)) {

            insertRequestStatement.setInt(1, request.getStudentId());
            insertRequestStatement.setInt(2, request.getProjectId());
            insertRequestStatement.setInt(3, request.getPriority());

            int affectedRows = insertRequestStatement.executeUpdate();
            logger.info("Solicitud de proyecto insertada: estudiante {} → proyecto {}", request.getStudentId(), request.getProjectId());
            return affectedRows > 0;
        } catch (SQLException sqlException) {
            logger.error("Error insertando solicitud de proyecto estudiante {} → proyecto {}", request.getStudentId(), request.getProjectId(), sqlException);
            throw new BusinessException("Error insertando solicitud de proyecto para estudiante " + request.getStudentId() + " y proyecto " + request.getProjectId(), sqlException);
        }
    }

    @Override
    public List<ProjectRequestDTO> findByStudent(int studentId) throws BusinessException {
        List<ProjectRequestDTO> requestList = new ArrayList<>();
        String selectRequestsQuery = "SELECT estudiante_id, proyecto_id, prioridad FROM SOLICITUD_PROYECTO WHERE estudiante_id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectRequests = connection.prepareStatement(selectRequestsQuery)) {

            statementSelectRequests.setInt(1, studentId);
            try (ResultSet resultSetRequests = statementSelectRequests.executeQuery()) {
                while (resultSetRequests.next()) {
                    ProjectRequestDTO request = new ProjectRequestDTO(
                        resultSetRequests.getInt("estudiante_id"),
                        resultSetRequests.getInt("proyecto_id"),
                        resultSetRequests.getInt("prioridad")
                    );
                    requestList.add(request);
                }
            }
            if (requestList.isEmpty()) {
                logger.warn("No se encontraron solicitudes de proyecto para estudiante {}", studentId);
            } else {
                logger.info("Se encontraron {} solicitudes de proyecto para estudiante {}", requestList.size(), studentId);
            }
            return requestList;
        } catch (SQLException sqlException) {
            logger.error("Error consultando solicitudes de proyecto para estudiante {}", studentId, sqlException);
            throw new BusinessException("Error consultando solicitudes de proyecto para estudiante id " + studentId, sqlException);
        }
    }

    @Override
    public boolean delete(ProjectRequestDTO request) throws BusinessException {
        String deleteRequestById = "DELETE FROM SOLICITUD_PROYECTO WHERE estudiante_id = ? AND proyecto_id = ?";
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteRequestStatement = connection.prepareStatement(deleteRequestById)) {

            deleteRequestStatement.setInt(1, request.getStudentId());
            deleteRequestStatement.setInt(2, request.getProjectId());

            int affectedRows = deleteRequestStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Solicitud de proyecto eliminada: estudiante {} → proyecto {}", request.getStudentId(), request.getProjectId());
                return true;
            } else {
                logger.warn("No se eliminó solicitud de proyecto: estudiante {} → proyecto {}", request.getStudentId(), request.getProjectId());
                return false;
            }
        } catch (SQLException sqlException) {
            logger.error("Error eliminando solicitud de proyecto estudiante {} → proyecto {}", request.getStudentId(), request.getProjectId(), sqlException);
            throw new BusinessException("Error eliminando solicitud de proyecto para estudiante " + request.getStudentId() + " y proyecto " + request.getProjectId(), sqlException);
        }
    }
}
