package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectDAO implements IProjectDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectDAO.class);

    private static final String INSERT_PROJECT_QUERY =
        "INSERT INTO PROYECTO "
        + "(nombre, descripcion_general, objetivo_general, "
        + "objetivos_inmediatos, objetivos_mediatos, metodologia, "
        + "recursos, responsabilidades, duracion, organizacion_id, "
        + "responsable_id, activo) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_PROJECTS_QUERY =
        "SELECT * FROM PROYECTO";

    private static final String SELECT_PROJECTS_BY_STATUS_QUERY =
        "SELECT * FROM PROYECTO WHERE activo = ?";

    private static final String UPDATE_PROJECT_QUERY =
        "UPDATE PROYECTO SET nombre = ?, "
        + "descripcion_general = ?, objetivo_general = ?, "
        + "objetivos_inmediatos = ?, objetivos_mediatos = ?, "
        + "metodologia = ?, recursos = ?, responsabilidades = ?, "
        + "duracion = ?, organizacion_id = ?, responsable_id = ?, "
        + "activo = ? WHERE id = ?";

    private static final String DELETE_PROJECT_QUERY =
        "DELETE FROM PROYECTO WHERE id = ?";

    @Override
    public boolean createProject(ProjectDTO project)
            throws BusinessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        boolean wasCreated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertProjectStatement =
                 connection.prepareStatement(
                     INSERT_PROJECT_QUERY
                 )) {
            setProjectData(insertProjectStatement, project);

            wasCreated =
                insertProjectStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error creando proyecto {}",
                project.getName(),
                sqlException
            );

            throw new BusinessException(
                "Error creando proyecto: "
                    + project.getName(),
                sqlException
            );
        }

        return wasCreated;
    }

    @Override
    public List<ProjectDTO> findAll()
            throws BusinessException {
        List<ProjectDTO> projects = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectAllProjectsStatement =
                 connection.prepareStatement(
                     SELECT_ALL_PROJECTS_QUERY
                 );
             ResultSet resultSet =
                 selectAllProjectsStatement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(buildProject(resultSet));
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error listando proyectos",
                sqlException
            );

            throw new BusinessException(
                "Error listando proyectos.",
                sqlException
            );
        }

        return List.copyOf(projects);
    }

    @Override
    public List<ProjectDTO> findByStatus(boolean isActive)
            throws BusinessException {
        List<ProjectDTO> projects = new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectProjectsByStatusStatement =
                 connection.prepareStatement(
                     SELECT_PROJECTS_BY_STATUS_QUERY
                 )) {
            selectProjectsByStatusStatement.setBoolean(
                1,
                isActive
            );

            try (ResultSet resultSet =
                    selectProjectsByStatusStatement.executeQuery()) {
                while (resultSet.next()) {
                    projects.add(buildProject(resultSet));
                }
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error listando proyectos con estado activo={}",
                isActive,
                sqlException
            );

            throw new BusinessException(
                "Error listando proyectos con estado activo="
                    + isActive,
                sqlException
            );
        }

        return List.copyOf(projects);
    }

    @Override
    public boolean update(ProjectDTO project)
            throws BusinessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        boolean wasUpdated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateProjectStatement =
                 connection.prepareStatement(
                     UPDATE_PROJECT_QUERY
                 )) {
            setProjectData(updateProjectStatement, project);

            updateProjectStatement.setInt(
                13,
                project.getId()
            );

            wasUpdated =
                updateProjectStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error actualizando proyecto con id {}",
                project.getId(),
                sqlException
            );

            throw new BusinessException(
                "Error actualizando proyecto con id "
                    + project.getId(),
                sqlException
            );
        }

        return wasUpdated;
    }

    @Override
    public boolean delete(int id)
            throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id del proyecto debe ser positivo."
        );

        boolean wasDeleted;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement deleteProjectStatement =
                 connection.prepareStatement(
                     DELETE_PROJECT_QUERY
                 )) {
            deleteProjectStatement.setInt(1, id);

            wasDeleted =
                deleteProjectStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error eliminando proyecto con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error eliminando proyecto con id "
                    + id,
                sqlException
            );
        }

        return wasDeleted;
    }

    private void setProjectData(
            PreparedStatement statement,
            ProjectDTO project
    ) throws SQLException {
        statement.setString(1, project.getName());
        statement.setString(
            2,
            project.getGeneralDescription()
        );
        statement.setString(
            3,
            project.getGeneralObjective()
        );
        statement.setString(
            4,
            project.getImmediateObjectives()
        );
        statement.setString(
            5,
            project.getMediateObjective()
        );
        statement.setString(
            6,
            project.getMethodology()
        );
        statement.setString(
            7,
            project.getResources()
        );
        statement.setString(
            8,
            project.getResponsibilities()
        );
        statement.setInt(
            9,
            project.getDuration()
        );
        statement.setInt(
            10,
            project.getLinkedOrganizationId()
        );
        statement.setInt(
            11,
            project.getProjectResponsibleId()
        );
        statement.setBoolean(
            12,
            project.getIsActive()
        );
    }

    private ProjectDTO buildProject(ResultSet resultSet)
            throws SQLException {
        ProjectDTO project =
            new ProjectDTO(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString(
                    "descripcion_general"
                ),
                resultSet.getString(
                    "objetivo_general"
                ),
                resultSet.getString(
                    "objetivos_inmediatos"
                ),
                resultSet.getString(
                    "objetivos_mediatos"
                ),
                resultSet.getString("metodologia"),
                resultSet.getString("recursos"),
                resultSet.getString(
                    "responsabilidades"
                ),
                resultSet.getInt("duracion"),
                resultSet.getInt(
                    "organizacion_id"
                ),
                resultSet.getInt(
                    "responsable_id"
                ),
                resultSet.getBoolean("activo")
            );

        return project;
    }
}