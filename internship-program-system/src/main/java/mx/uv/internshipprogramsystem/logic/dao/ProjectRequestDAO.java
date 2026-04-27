package mx.uv.internshipprogramsystem.logic.dao;

import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectRequestException;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectRequestDAO;

public class ProjectRequestDAO implements IProjectRequestDAO {

    @Override
    public boolean insert(ProjectRequestDTO request) throws ProjectRequestException {
        boolean operationSuccessful = false;
        String insertRequestQuery = "INSERT INTO SOLICITUD_PROYECTO (estudiante_id, proyecto_id, prioridad) VALUES (?, ?, ?)";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement insertRequestStatement = Connection.prepareStatement(insertRequestQuery)) {

            insertRequestStatement.setInt(1, request.getStudentId());
            insertRequestStatement.setInt(2, request.getProjectId());
            insertRequestStatement.setInt(3, request.getPriority());

            int affectedRows = insertRequestStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectRequestException("Error inserting project request", exception);
        }
        return operationSuccessful;
    }

    @Override
    public List<ProjectRequestDTO> findByStudent(int studentId) throws ProjectRequestException {
        List<ProjectRequestDTO> requestList = new ArrayList<>();
        String selectRequestsQuery = "SELECT estudiante_id, proyecto_id, prioridad FROM SOLICITUD_PROYECTO WHERE estudiante_id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement statementSelectRequests = Connection.prepareStatement(selectRequestsQuery)) {

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
        } catch (SQLException exception) {
            throw new ProjectRequestException("Error finding project requests for student id " + studentId, exception);
        }
        return requestList;
    }

    @Override
    public boolean delete(ProjectRequestDTO request) throws ProjectRequestException {
        boolean operationSuccessful = false;
        String deleteRequestById = "DELETE FROM SOLICITUD_PROYECTO WHERE estudiante_id = ? AND proyecto_id = ?";

        try (Connection Connection = DataBaseManager.getConnection();
             PreparedStatement deleteRequestStatement = Connection.prepareStatement(deleteRequestById)) {

            deleteRequestStatement.setInt(1, request.getStudentId());
            deleteRequestStatement.setInt(2, request.getProjectId());

            int affectedRows = deleteRequestStatement.executeUpdate();
            if (affectedRows > 0) {
                operationSuccessful = true;
            }
        } catch (SQLException exception) {
            throw new ProjectRequestException("Error deleting project request for student id " 
                                              + request.getStudentId() + " and project id " 
                                              + request.getProjectId(), exception);
        }
        return operationSuccessful;
    }
}
