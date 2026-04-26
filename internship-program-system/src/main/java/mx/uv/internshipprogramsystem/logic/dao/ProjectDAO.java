package mx.uv.internshipprogramsystem.logic.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectDAO;

public class ProjectDAO implements IProjectDAO{
    private static final Logger logger = LoggerFactory.getLogger(ProjectDAO.class);

    public ProjectDAO() {
    }

    public boolean createProject(ProjectDTO project) throws BusinessException {
        String insertProjectQuery =
            "INSERT INTO PROYECTO " +
            "(nombre, descripcion_general, objetivo_general, objetivos_inmediatos, objetivos_mediatos, metodologia, recursos, responsabilidades, duracion, organizacion_id, responsable_id, activo) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement insertProjectStatement = connection.prepareStatement(insertProjectQuery)) {
            insertProjectStatement.setString(1, project.getName());
            insertProjectStatement.setString(2, project.getGeneralDescription());
            insertProjectStatement.setString(3, project.getGeneralObjetive());
            insertProjectStatement.setString(4, project.getImmediateObjetives());
            insertProjectStatement.setString(5, project.getMediateObjetive());
            insertProjectStatement.setString(6, project.getMethodology());
            insertProjectStatement.setString(7, project.getResources());
            insertProjectStatement.setString(8, project.getResponsabilities());
            insertProjectStatement.setInt(9, project.getDuration());
            insertProjectStatement.setInt(10, project.getLinkedOrganizationId());
            insertProjectStatement.setInt(11, project.getProjectResponsibleId());
            insertProjectStatement.setBoolean(12, project.getIsActive());
            return insertProjectStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error creando proyecto: " + project.getName(), sqlException);
        }
    }

    public List<ProjectDTO> findAll() throws BusinessException {
        String selectAllProjectsQuery = "SELECT * FROM PROYECTO";
        List<ProjectDTO> projects = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                Statement selectAllProjectsStatement = connection.createStatement();
             ResultSet resultSet = selectAllProjectsStatement.executeQuery(selectAllProjectsQuery)) {

            while (resultSet.next()) {
                projects.add(new ProjectDTO(
                    resultSet.getInt("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("descripcion_general"),
                    resultSet.getString("objetivo_general"),
                    resultSet.getString("objetivos_inmediatos"),
                    resultSet.getString("objetivos_mediatos"),
                    resultSet.getString("metodologia"),
                    resultSet.getString("recursos"),
                    resultSet.getString("responsabilidades"),
                    resultSet.getInt("duracion"),
                    resultSet.getInt("organizacion_id"),
                    resultSet.getInt("responsable_id"),
                    resultSet.getBoolean("activo")
                ));
            }
            return projects;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error listando proyectos", sqlException);
        }
    }

    public List<ProjectDTO> findByStatus(boolean isActive) throws BusinessException {
        String selectProjectsByStatusQuery = "SELECT * FROM PROYECTO WHERE activo=?";
        List<ProjectDTO> projects = new ArrayList<>();
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement selectProjectsByStatusStatement = connection.prepareStatement(selectProjectsByStatusQuery)) {
            selectProjectsByStatusStatement.setBoolean(1, isActive);
            try (ResultSet resultSet = selectProjectsByStatusStatement.executeQuery()) {
                while (resultSet.next()) {
                    projects.add(new ProjectDTO(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion_general"),
                        resultSet.getString("objetivo_general"),
                        resultSet.getString("objetivos_inmediatos"),
                        resultSet.getString("objetivos_mediatos"),
                        resultSet.getString("metodologia"),
                        resultSet.getString("recursos"),
                        resultSet.getString("responsabilidades"),
                        resultSet.getInt("duracion"),
                        resultSet.getInt("organizacion_id"),
                        resultSet.getInt("responsable_id"),
                        resultSet.getBoolean("activo")
                    ));
                }
            }
            return projects;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error listando proyectos con estado activo=" + isActive, sqlException);
        }
    }

    public boolean update(ProjectDTO project) throws BusinessException {
        String updateProjectQuery =
            "UPDATE PROYECTO SET nombre=?, descripcion_general=?, objetivo_general=?, objetivos_inmediatos=?, objetivos_mediatos=?, metodologia=?, recursos=?, responsabilidades=?, duracion=?, organizacion_id=?, responsable_id=?, activo=? WHERE id=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement updateProjectStatement = connection.prepareStatement(updateProjectQuery)) {
            updateProjectStatement.setString(1, project.getName());
            updateProjectStatement.setString(2, project.getGeneralDescription());
            updateProjectStatement.setString(3, project.getGeneralObjetive());
            updateProjectStatement.setString(4, project.getImmediateObjetives());
            updateProjectStatement.setString(5, project.getMediateObjetive());
            updateProjectStatement.setString(6, project.getMethodology());
            updateProjectStatement.setString(7, project.getResources());
            updateProjectStatement.setString(8, project.getResponsabilities());
            updateProjectStatement.setInt(9, project.getDuration());
            updateProjectStatement.setInt(10, project.getLinkedOrganizationId());
            updateProjectStatement.setInt(11, project.getProjectResponsibleId());
            updateProjectStatement.setBoolean(12, project.getIsActive());
            updateProjectStatement.setInt(13, project.getId());
            return updateProjectStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error actualizando proyecto con id=" + project.getId(), sqlException);
        }
    }

    public boolean delete(int id) throws BusinessException {
        String deleteProjectQuery = "DELETE FROM PROYECTO WHERE id=?";
        try (Connection connection = DataBaseManager.getConnection();
                PreparedStatement deleteProjectStatement = connection.prepareStatement(deleteProjectQuery)) {
            deleteProjectStatement.setInt(1, id);
            return deleteProjectStatement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            throw new BusinessException("Error eliminando proyecto con id=" + id, sqlException);
        }
    }
}

