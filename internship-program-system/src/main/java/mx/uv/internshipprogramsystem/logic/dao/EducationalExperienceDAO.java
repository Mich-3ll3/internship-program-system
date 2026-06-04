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
import mx.uv.internshipprogramsystem.logic.dto.EducationalExperienceDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IEducationalExperienceDAO;
import mx.uv.internshipprogramsystem.logic.validations.EducationalExperienceValidator;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class EducationalExperienceDAO implements IEducationalExperienceDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(EducationalExperienceDAO.class);

    private static final String INSERT_EDUCATIONAL_EXPERIENCE_QUERY =
        "INSERT INTO EXPERIENCIA_EDUCATIVA "
        + "(NRC, periodo_escolar, seccion, "
        + "profesor_id, activa) "
        + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_EDUCATIONAL_EXPERIENCE_BY_NRC_QUERY =
        "SELECT NRC, periodo_escolar, seccion, "
        + "profesor_id, activa "
        + "FROM EXPERIENCIA_EDUCATIVA "
        + "WHERE NRC = ?";

    private static final String SELECT_ALL_EDUCATIONAL_EXPERIENCES_QUERY =
        "SELECT NRC, periodo_escolar, seccion, "
        + "profesor_id, activa "
        + "FROM EXPERIENCIA_EDUCATIVA";

    @Override
    public boolean create(
            EducationalExperienceDTO educationalExperience
    ) throws BusinessException {
        EducationalExperienceValidator validator = new EducationalExperienceValidator();
        validator.validateForCreation(educationalExperience);
        boolean wasCreated;
        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertStatement =
                    connection.prepareStatement(
                        INSERT_EDUCATIONAL_EXPERIENCE_QUERY
                    )) {
            insertStatement.setString(1, educationalExperience.getNrc());
            insertStatement.setString(2, educationalExperience.getSchoolPeriod());
            insertStatement.setString(3, educationalExperience.getSection());
            insertStatement.setInt(4,educationalExperience.getProfessorId());
            insertStatement.setBoolean(5, educationalExperience.getIsActive());

            wasCreated = insertStatement.executeUpdate() > 0;

            LOGGER.info(
                "Experiencia educativa registrada con NRC {}",
                educationalExperience.getNrc()
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
                "Violación de integridad al registrar experiencia educativa",
                integrityException
            );

            throw new BusinessException(
                "El NRC ya existe o el profesor asociado no es válido.",
                integrityException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al registrar experiencia educativa",
                sqlException
            );

            throw new BusinessException(
                "Error al registrar la experiencia educativa.",
                sqlException
            );
        }

        return wasCreated;
    }

    @Override
    public Optional<EducationalExperienceDTO> findByNrc(
            String nrc
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            nrc,
            "El NRC no puede estar vacío."
        );

        Optional<EducationalExperienceDTO> educationalExperience;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        SELECT_EDUCATIONAL_EXPERIENCE_BY_NRC_QUERY
                    )) {
            selectStatement.setString(1, nrc);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                educationalExperience =
                    buildOptionalEducationalExperience(resultSet);
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
                "Error SQL al buscar experiencia educativa",
                sqlException
            );

            throw new BusinessException(
                "Error al buscar experiencia educativa con NRC " + nrc,
                sqlException
            );
        }

        return educationalExperience;
    }

    @Override
    public List<EducationalExperienceDTO> findAll()
            throws BusinessException {
        List<EducationalExperienceDTO> educationalExperiences =
            new ArrayList<>();

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectStatement =
                    connection.prepareStatement(
                        SELECT_ALL_EDUCATIONAL_EXPERIENCES_QUERY
                    );
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                educationalExperiences.add(
                    buildEducationalExperience(resultSet)
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
                "Error SQL al obtener experiencias educativas",
                sqlException
            );

            throw new BusinessException(
                "Error al obtener experiencias educativas.",
                sqlException
            );
        }

        return List.copyOf(educationalExperiences);
    }

    private Optional<EducationalExperienceDTO>
            buildOptionalEducationalExperience(
                    ResultSet resultSet
    ) throws SQLException {
        Optional<EducationalExperienceDTO> educationalExperience;

        if (resultSet.next()) {
            educationalExperience =
                Optional.of(
                    buildEducationalExperience(resultSet)
                );
        } else {
            educationalExperience = Optional.empty();
        }

        return educationalExperience;
    }

    private EducationalExperienceDTO buildEducationalExperience(
            ResultSet resultSet
    ) throws SQLException {
        EducationalExperienceDTO educationalExperience =
            new EducationalExperienceDTO(
                resultSet.getString("NRC"),
                resultSet.getString("periodo_escolar"),
                resultSet.getString("seccion"),
                resultSet.getInt("profesor_id"),
                resultSet.getBoolean("activa")
            );

        return educationalExperience;
    }
}