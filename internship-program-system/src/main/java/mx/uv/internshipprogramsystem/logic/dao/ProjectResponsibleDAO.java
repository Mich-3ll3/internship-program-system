package mx.uv.internshipprogramsystem.logic.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectResponsibleDAO
        implements IProjectResponsibleDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectResponsibleDAO.class
        );

    private static final String INSERT_RESPONSIBLE_QUERY =
        "INSERT INTO RESPONSABLE_PROYECTO "
        + "(nombre, apellido_paterno, apellido_materno, "
        + "correo, cargo, organizacion_id) "
        + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_RESPONSIBLE_BY_ID_QUERY =
        "SELECT id, nombre, apellido_paterno, "
        + "apellido_materno, correo, cargo, "
        + "organizacion_id "
        + "FROM RESPONSABLE_PROYECTO "
        + "WHERE id = ?";

    private static final String DELETE_RESPONSIBLE_QUERY =
        "DELETE FROM RESPONSABLE_PROYECTO "
        + "WHERE id = ?";

    @Override
    public boolean insert(ProjectResponsibleDTO responsible)
            throws BusinessException {
        InputValidator.validateNotNull(
            responsible,
            "ProjectResponsibleDTO no puede ser nulo."
        );

        boolean wasInserted;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement insertResponsibleStatement =
                 connection.prepareStatement(
                     INSERT_RESPONSIBLE_QUERY
                 )) {
            insertResponsibleStatement.setString(
                1,
                responsible.getFirstName()
            );
            insertResponsibleStatement.setString(
                2,
                responsible.getLastNameFather()
            );
            insertResponsibleStatement.setString(
                3,
                responsible.getLastNameMother()
            );
            insertResponsibleStatement.setString(
                4,
                responsible.getEmail()
            );
            insertResponsibleStatement.setString(
                5,
                responsible.getPosition()
            );
            insertResponsibleStatement.setInt(
                6,
                responsible.getOrganizationId()
            );

            wasInserted =
                insertResponsibleStatement.executeUpdate() > 0;

            LOGGER.info(
                "Responsable de proyecto {} "
                    + "insertado correctamente",
                responsible.getEmail()
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
                "Error insertando responsable "
                    + "de proyecto {}",
                responsible.getEmail(),
                sqlException
            );

            throw new BusinessException(
                "Error insertando responsable "
                    + "de proyecto "
                    + responsible.getEmail(),
                sqlException
            );
        }

        return wasInserted;
    }

    @Override
    public Optional<ProjectResponsibleDTO> findById(int id)
            throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id del responsable debe ser positivo."
        );

        Optional<ProjectResponsibleDTO> responsible;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement selectResponsibleStatement =
                 connection.prepareStatement(
                     SELECT_RESPONSIBLE_BY_ID_QUERY
                 )) {
            selectResponsibleStatement.setInt(1, id);

            try (ResultSet resultSetResponsible =
                    selectResponsibleStatement.executeQuery()) {
                responsible =
                    buildOptionalResponsible(
                        resultSetResponsible,
                        id
                    );
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
                "Error buscando responsable "
                    + "de proyecto con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error buscando responsable "
                    + "de proyecto con id "
                    + id,
                sqlException
            );
        }

        return responsible;
    }

    @Override
    public boolean delete(int id)
            throws BusinessException {
        InputValidator.validatePositive(
            id,
            "El id del responsable debe ser positivo."
        );

        boolean wasDeleted;

        try (Connection connection =
                DataBaseManager.getConnection();
             PreparedStatement deleteResponsibleStatement =
                 connection.prepareStatement(
                     DELETE_RESPONSIBLE_QUERY
                 )) {
            deleteResponsibleStatement.setInt(1, id);

            wasDeleted =
                deleteResponsibleStatement.executeUpdate() > 0;

            logDeleteResult(id, wasDeleted);
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
                "Error eliminando responsable "
                    + "de proyecto con id {}",
                id,
                sqlException
            );

            throw new BusinessException(
                "Error eliminando responsable "
                    + "de proyecto con id "
                    + id,
                sqlException
            );
        }

        return wasDeleted;
    }

    private Optional<ProjectResponsibleDTO>
            buildOptionalResponsible(
                ResultSet resultSetResponsible,
                int id
    ) throws SQLException {
        Optional<ProjectResponsibleDTO> responsible;

        if (resultSetResponsible.next()) {
            LOGGER.info(
                "Responsable de proyecto "
                    + "con id {} encontrado",
                id
            );

            responsible =
                Optional.of(
                    buildResponsible(
                        resultSetResponsible
                    )
                );
        } else {
            LOGGER.warn(
                "No se encontró responsable "
                    + "de proyecto con id {}",
                id
            );

            responsible = Optional.empty();
        }

        return responsible;
    }

    private void logDeleteResult(
            int id,
            boolean wasDeleted
    ) {
        if (wasDeleted) {
            LOGGER.info(
                "Responsable de proyecto "
                    + "con id {} eliminado correctamente",
                id
            );
        } else {
            LOGGER.warn(
                "No se eliminó responsable "
                    + "de proyecto con id {}",
                id
            );
        }
    }

    private ProjectResponsibleDTO buildResponsible(
            ResultSet resultSetResponsible
    ) throws SQLException {
        ProjectResponsibleDTO responsible =
            new ProjectResponsibleDTO(
                resultSetResponsible.getInt("id"),
                resultSetResponsible.getString("nombre"),
                resultSetResponsible.getString(
                    "apellido_paterno"
                ),
                resultSetResponsible.getString(
                    "apellido_materno"
                ),
                resultSetResponsible.getString("correo"),
                resultSetResponsible.getString("cargo"),
                resultSetResponsible.getInt(
                    "organizacion_id"
                )
            );

        return responsible;
    }
}