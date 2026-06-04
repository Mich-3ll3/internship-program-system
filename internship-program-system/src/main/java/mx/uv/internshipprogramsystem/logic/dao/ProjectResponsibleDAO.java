package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectResponsibleDAO implements IProjectResponsibleDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectResponsibleDAO.class);

    private static final String INSERT_RESPONSIBLE_QUERY =
        "INSERT INTO RESPONSABLE_PROYECTO "
        + "(nombre, apellido_paterno, apellido_materno, "
        + "correo, cargo, organizacion_id) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_RESPONSIBLE_BY_ID_QUERY =
        "SELECT rp.id, rp.nombre, rp.apellido_paterno, "
        + "rp.apellido_materno, rp.correo, rp.cargo, "
        + "rp.organizacion_id, ov.nombre AS nombre_organizacion "
        + "FROM RESPONSABLE_PROYECTO rp "
        + "INNER JOIN ORGANIZACION_VINCULADA ov "
        + "ON rp.organizacion_id = ov.id "
        + "WHERE rp.id = ?";

    private static final String SELECT_ALL_RESPONSIBLES_QUERY =
        "SELECT rp.id, rp.nombre, rp.apellido_paterno, "
        + "rp.apellido_materno, rp.correo, rp.cargo, "
        + "rp.organizacion_id, ov.nombre AS nombre_organizacion, "
        + "p.nombre AS nombre_proyecto "
        + "FROM RESPONSABLE_PROYECTO rp "
        + "INNER JOIN ORGANIZACION_VINCULADA ov "
        + "ON rp.organizacion_id = ov.id "
        + "LEFT JOIN PROYECTO p "
        + "ON p.responsable_id = rp.id "
        + "ORDER BY rp.nombre, rp.apellido_paterno, rp.apellido_materno";

    private static final String SEARCH_RESPONSIBLES_QUERY =
        "SELECT rp.id, rp.nombre, rp.apellido_paterno, "
        + "rp.apellido_materno, rp.correo, rp.cargo, "
        + "rp.organizacion_id, ov.nombre AS nombre_organizacion, "
        + "p.nombre AS nombre_proyecto "
        + "FROM RESPONSABLE_PROYECTO rp "
        + "INNER JOIN ORGANIZACION_VINCULADA ov "
        + "ON rp.organizacion_id = ov.id "
        + "LEFT JOIN PROYECTO p "
        + "ON p.responsable_id = rp.id "
        + "WHERE rp.nombre LIKE ? "
        + "OR rp.apellido_paterno LIKE ? "
        + "OR rp.apellido_materno LIKE ? "
        + "OR rp.correo LIKE ? "
        + "OR rp.cargo LIKE ? "
        + "OR ov.nombre LIKE ? "
        + "OR p.nombre LIKE ? "
        + "ORDER BY rp.nombre, rp.apellido_paterno, rp.apellido_materno";

    private static final String DELETE_RESPONSIBLE_QUERY =
        "DELETE FROM RESPONSABLE_PROYECTO "
        + "WHERE id = ?";

    @Override
    public boolean insert(
            ProjectResponsibleDTO responsible
    ) throws BusinessException {
        InputValidator.validateNotNull(
            responsible,
            "Los datos del responsable no pueden ser nulos."
        );

        boolean wasInserted;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        INSERT_RESPONSIBLE_QUERY
                    )) {
            setResponsibleParameters(
                statement,
                responsible
            );

            wasInserted =
                statement.executeUpdate() > 0;

            LOGGER.info(
                "Responsable de proyecto registrado correctamente."
            );
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (
                SQLIntegrityConstraintViolationException integrityException
        ) {
            LOGGER.error(
                "Violación de integridad al registrar responsable",
                integrityException
            );

            throw new BusinessException(
                "Ya existe un responsable con ese correo "
                + "o la organización no es válida.",
                integrityException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al registrar responsable de proyecto",
                sqlException
            );

            throw new BusinessException(
                "Error al registrar el responsable de proyecto.",
                sqlException
            );
        }

        return wasInserted;
    }

    @Override
    public Optional<ProjectResponsibleDTO> findById(
            int id
    ) throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id del responsable debe ser positivo."
        );

        Optional<ProjectResponsibleDTO> responsible;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        SELECT_RESPONSIBLE_BY_ID_QUERY
                    )) {
            statement.setInt(
                1,
                id
            );

            responsible =
                executeResponsibleSearch(
                    statement
                );
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
                "Error SQL al buscar responsable de proyecto",
                sqlException
            );

            throw new BusinessException(
                "Error al buscar el responsable de proyecto.",
                sqlException
            );
        }

        return responsible;
    }

    @Override
    public List<ProjectResponsibleDTO> findAll()
            throws BusinessException {
        List<ProjectResponsibleDTO> responsibles;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        SELECT_ALL_RESPONSIBLES_QUERY
                    )) {
            responsibles =
                executeResponsibleListSearch(
                    statement
                );

            LOGGER.info(
                "Consulta de responsables de proyecto realizada correctamente."
            );
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
                "Error SQL al consultar responsables de proyecto",
                sqlException
            );

            throw new BusinessException(
                "Error al consultar los responsables de proyecto.",
                sqlException
            );
        }

        return List.copyOf(responsibles);
    }

    @Override
    public List<ProjectResponsibleDTO> findBySearchText(
            String searchText
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            searchText,
            "El texto de búsqueda no puede estar vacío."
        );

        List<ProjectResponsibleDTO> responsibles;
        String searchPattern = "%" + searchText.trim() + "%";

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        SEARCH_RESPONSIBLES_QUERY
                    )) {
            setSearchParameters(
                statement,
                searchPattern
            );

            responsibles =
                executeResponsibleListSearch(
                    statement
                );

            LOGGER.info(
                "Búsqueda de responsables de proyecto realizada correctamente."
            );
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
                "Error SQL al buscar responsables de proyecto",
                sqlException
            );

            throw new BusinessException(
                "Error al buscar responsables de proyecto.",
                sqlException
            );
        }

        return List.copyOf(responsibles);
    }

    @Override
    public boolean delete(
            int id
    ) throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id del responsable debe ser positivo."
        );

        boolean wasDeleted;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        DELETE_RESPONSIBLE_QUERY
                    )) {
            statement.setInt(
                1,
                id
            );

            wasDeleted =
                statement.executeUpdate() > 0;

            logDeleteResult(
                wasDeleted
            );
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
                "Error SQL al eliminar responsable de proyecto",
                sqlException
            );

            throw new BusinessException(
                "Error al eliminar el responsable de proyecto.",
                sqlException
            );
        }

        return wasDeleted;
    }

    private void setResponsibleParameters(
            PreparedStatement statement,
            ProjectResponsibleDTO responsible
    ) throws SQLException {
        statement.setString(
            1,
            responsible.getFirstName()
        );

        statement.setString(
            2,
            responsible.getLastNameFather()
        );

        statement.setString(
            3,
            responsible.getLastNameMother()
        );

        statement.setString(
            4,
            responsible.getEmail()
        );

        statement.setString(
            5,
            responsible.getPosition()
        );

        statement.setInt(
            6,
            responsible.getOrganizationId()
        );
    }

    private void setSearchParameters(
            PreparedStatement statement,
            String searchPattern
    ) throws SQLException {
        statement.setString(
            1,
            searchPattern
        );

        statement.setString(
            2,
            searchPattern
        );

        statement.setString(
            3,
            searchPattern
        );

        statement.setString(
            4,
            searchPattern
        );

        statement.setString(
            5,
            searchPattern
        );

        statement.setString(
            6,
            searchPattern
        );
        statement.setString(
            7,
            searchPattern
        );
    }

    private Optional<ProjectResponsibleDTO> executeResponsibleSearch(
            PreparedStatement statement
    ) throws SQLException {
        Optional<ProjectResponsibleDTO> responsible;

        try (ResultSet resultSet = statement.executeQuery()) {
            responsible =
                buildOptionalResponsible(
                    resultSet
                );
        }

        return responsible;
    }

    private List<ProjectResponsibleDTO> executeResponsibleListSearch(
            PreparedStatement statement
    ) throws SQLException {
        List<ProjectResponsibleDTO> responsibles;

        try (ResultSet resultSet = statement.executeQuery()) {
            responsibles =
                buildResponsibleList(
                    resultSet
                );
        }

        return responsibles;
    }

    private Optional<ProjectResponsibleDTO> buildOptionalResponsible(
            ResultSet resultSet
    ) throws SQLException {
        Optional<ProjectResponsibleDTO> responsible;

        if (resultSet.next()) {
            responsible =
                Optional.of(
                    buildResponsible(
                        resultSet
                    )
                );
        } else {
            responsible =
                Optional.empty();
        }

        return responsible;
    }

    private List<ProjectResponsibleDTO> buildResponsibleList(
            ResultSet resultSet
    ) throws SQLException {
        List<ProjectResponsibleDTO> responsibles =
            new ArrayList<>();

        while (resultSet.next()) {
            responsibles.add(
                buildResponsible(
                    resultSet
                )
            );
        }

        return responsibles;
    }

    private ProjectResponsibleDTO buildResponsible(
            ResultSet resultSet
    ) throws SQLException {
        ProjectResponsibleDTO responsible =
            new ProjectResponsibleDTO(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("apellido_paterno"),
                resultSet.getString("apellido_materno"),
                resultSet.getString("correo"),
                resultSet.getString("cargo"),
                resultSet.getInt("organizacion_id"),
                resultSet.getString("nombre_organizacion"),
                resultSet.getString("nombre_proyecto")
            );

        return responsible;
    }

    private void logDeleteResult(
            boolean wasDeleted
    ) {
        if (wasDeleted) {
            LOGGER.info(
                "Responsable de proyecto eliminado correctamente."
            );
        } else {
            LOGGER.warn(
                "No se eliminó el responsable de proyecto."
            );
        }
    }
}