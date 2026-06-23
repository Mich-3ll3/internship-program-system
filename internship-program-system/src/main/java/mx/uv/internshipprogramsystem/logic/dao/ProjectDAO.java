package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
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
        + "recursos, responsabilidades, organizacion_id, "
        + "responsable_id, activo) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_PROJECTS_QUERY =
        "SELECT * "
        + "FROM PROYECTO "
        + "WHERE activo = TRUE";

    private static final String SELECT_PROJECT_BY_ID_QUERY =
        "SELECT * "
        + "FROM PROYECTO "
        + "WHERE id = ?";

    private static final String SELECT_PROJECTS_BY_STATUS_QUERY =
        "SELECT * "
        + "FROM PROYECTO "
        + "WHERE activo = ?";

    private static final String COUNT_ALL_PROJECTS_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM PROYECTO "
        + "WHERE activo = TRUE";

    private static final String UPDATE_PROJECT_QUERY =
        "UPDATE PROYECTO SET nombre = ?, "
        + "descripcion_general = ?, objetivo_general = ?, "
        + "objetivos_inmediatos = ?, objetivos_mediatos = ?, "
        + "metodologia = ?, recursos = ?, responsabilidades = ?, "
        + "organizacion_id = ?, responsable_id = ?, "
        + "activo = ? "
        + "WHERE id = ?";

    private static final String DEACTIVATE_PROJECT_QUERY =
        "UPDATE PROYECTO "
        + "SET activo = FALSE "
        + "WHERE id = ?";

    @Override
    public boolean create(ProjectDTO project)
            throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        boolean wasCreated;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement insertProjectStatement =
                 connection.prepareStatement(INSERT_PROJECT_QUERY)) {
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
            throws BusinessException, DataAccessException {
        List<ProjectDTO> projects = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement selectAllProjectsStatement =
                 connection.prepareStatement(SELECT_ALL_PROJECTS_QUERY);
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
                "Error listando proyectos activos",
                sqlException
            );

            throw new BusinessException(
                "Error listando proyectos activos.",
                sqlException
            );
        }

        return List.copyOf(projects);
    }

    @Override
    public Optional<ProjectDTO> findById(int id)
            throws BusinessException, DataAccessException {
        InputValidator.validatePositive(
            id,
            "El id del proyecto debe ser positivo."
        );

        Optional<ProjectDTO> project = Optional.empty();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement selectProjectByIdStatement =
                 connection.prepareStatement(SELECT_PROJECT_BY_ID_QUERY)) {
            selectProjectByIdStatement.setInt(
                1,
                id
            );

            try (ResultSet resultSet =
                    selectProjectByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    project = Optional.of(buildProject(resultSet));
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
                "Error buscando proyecto con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error buscando proyecto con id " + id,
                sqlException
            );
        }

        return project;
    }

    @Override
    public List<ProjectDTO> findByStatus(boolean isActive)
            throws BusinessException, DataAccessException {
        List<ProjectDTO> projects = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
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
    public int countAll()
            throws BusinessException, DataAccessException {
        int totalProjects = 0;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement countAllProjectsStatement =
                 connection.prepareStatement(COUNT_ALL_PROJECTS_QUERY);
             ResultSet resultSet =
                 countAllProjectsStatement.executeQuery()) {
            if (resultSet.next()) {
                totalProjects = resultSet.getInt("total");
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
                "Error contando proyectos activos",
                sqlException
            );

            throw new BusinessException(
                "Error contando proyectos activos.",
                sqlException
            );
        }

        return totalProjects;
    }

    @Override
    public boolean update(ProjectDTO project)
            throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        InputValidator.validatePositive(
            project.getId(),
            "El id del proyecto debe ser positivo."
        );

        boolean wasUpdated;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement updateProjectStatement =
                 connection.prepareStatement(UPDATE_PROJECT_QUERY)) {
            setProjectData(updateProjectStatement, project);

            updateProjectStatement.setInt(
                12,
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
    public boolean update(
            ProjectDTO project,
            Connection connection
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );

        InputValidator.validatePositive(
            project.getId(),
            "El id del proyecto debe ser positivo."
        );

        boolean wasUpdated;

        try (PreparedStatement updateProjectStatement =
                connection.prepareStatement(UPDATE_PROJECT_QUERY)) {
            setProjectData(updateProjectStatement, project);

            updateProjectStatement.setInt(
                12,
                project.getId()
            );

            wasUpdated =
                updateProjectStatement.executeUpdate() > 0;
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
    public boolean deactivate(int id)
            throws BusinessException, DataAccessException {
        InputValidator.validatePositive(
            id,
            "El id del proyecto debe ser positivo."
        );

        boolean wasDeactivated;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement deactivateProjectStatement =
                 connection.prepareStatement(
                     DEACTIVATE_PROJECT_QUERY
                 )) {
            deactivateProjectStatement.setInt(
                1,
                id
            );

            wasDeactivated =
                deactivateProjectStatement.executeUpdate() > 0;
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
                "Error desactivando proyecto con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error desactivando proyecto con id " + id,
                sqlException
            );
        }

        return wasDeactivated;
    }
    
    @Override
    public int createAndReturnId(
            ProjectDTO project,
            Connection connection
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotNull(
            project,
            "ProjectDTO no puede ser nulo."
        );

        InputValidator.validateNotNull(
            connection,
            "La conexión no puede ser nula."
        );

        int generatedId = 0;

        try (PreparedStatement insertProjectStatement =
                connection.prepareStatement(
                    INSERT_PROJECT_QUERY,
                    Statement.RETURN_GENERATED_KEYS
                )) {
            setProjectData(
                insertProjectStatement,
                project
            );

            insertProjectStatement.executeUpdate();

            try (ResultSet resultSet =
                    insertProjectStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = resultSet.getInt(1);
                }
            }
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error creando proyecto y obteniendo id generado",
                sqlException
            );

            throw new BusinessException(
                "Error creando proyecto.",
                sqlException
            );
        }

        if (generatedId <= 0) {
            throw new BusinessException(
                "No se pudo obtener el identificador del proyecto."
            );
        }

        return generatedId;
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
            project.getLinkedOrganizationId()
        );
        statement.setInt(
            10,
            project.getProjectResponsibleId()
        );
        statement.setBoolean(
            11,
            project.getIsActive()
        );
    }

    private ProjectDTO buildProject(ResultSet resultSet)
            throws SQLException {
        ProjectDTO project = new ProjectDTO(
            resultSet.getInt("id"),
            resultSet.getString("nombre"),
            resultSet.getString("descripcion_general"),
            resultSet.getString("objetivo_general"),
            resultSet.getString("objetivos_inmediatos"),
            resultSet.getString("objetivos_mediatos"),
            resultSet.getString("metodologia"),
            resultSet.getString("recursos"),
            resultSet.getString("responsabilidades"),
            resultSet.getInt("organizacion_id"),
            resultSet.getInt("responsable_id"),
            resultSet.getBoolean("activo")
        );

        return project;
    }
}