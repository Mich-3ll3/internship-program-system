package mx.uv.internshipprogramsystem.logic.dao;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.Date;
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

import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;
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
        + "profesor_id, activa, fecha_inicio, fecha_fin) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_EDUCATIONAL_EXPERIENCE_BY_NRC_QUERY =
        "SELECT ee.NRC, ee.periodo_escolar, ee.seccion, "
        + "ee.profesor_id, ee.activa, ee.fecha_inicio, ee.fecha_fin, "
        + "CONCAT(u.nombre, ' ', u.apellido_paterno, ' ', "
        + "COALESCE(u.apellido_materno, '')) AS profesor_nombre "
        + "FROM EXPERIENCIA_EDUCATIVA ee "
        + "LEFT JOIN USUARIO u ON ee.profesor_id = u.id "
        + "WHERE ee.NRC = ?";

    private static final String SELECT_ALL_EDUCATIONAL_EXPERIENCES_QUERY =
        "SELECT ee.NRC, ee.periodo_escolar, ee.seccion, "
        + "ee.profesor_id, ee.activa, ee.fecha_inicio, ee.fecha_fin, "
        + "CONCAT(u.nombre, ' ', u.apellido_paterno, ' ', "
        + "COALESCE(u.apellido_materno, '')) AS profesor_nombre "
        + "FROM EXPERIENCIA_EDUCATIVA ee "
        + "LEFT JOIN USUARIO u ON ee.profesor_id = u.id";

    private static final String EXISTS_SECTION_BY_PERIOD_QUERY =
        "SELECT COUNT(*) AS total "
        + "FROM EXPERIENCIA_EDUCATIVA "
        + "WHERE periodo_escolar = ? "
        + "AND seccion = ?";

    @Override
    public boolean create(
            EducationalExperienceDTO educationalExperience
    ) throws BusinessException, DataAccessException {
        EducationalExperienceValidator validator = new EducationalExperienceValidator();
        validator.validateForCreation(educationalExperience);
        boolean wasCreated;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement insertStatement =
                    connection.prepareStatement(
                        INSERT_EDUCATIONAL_EXPERIENCE_QUERY
                    )) {
            insertStatement.setString(1, educationalExperience.getNrc());
            insertStatement.setString(2, educationalExperience.getSchoolPeriod());
            insertStatement.setString(3, educationalExperience.getSection());
            insertStatement.setInt(4,educationalExperience.getProfessorId());
            insertStatement.setBoolean(5, educationalExperience.getIsActive());
            insertStatement.setDate(
                6,
                Date.valueOf(
                    educationalExperience.getStartDate()
                )
            );
            insertStatement.setDate(
                7,
                Date.valueOf(
                    educationalExperience.getEndDate()
                )
            );

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
    ) throws BusinessException, DataAccessException {
        InputValidator.validateNotEmpty(
            nrc,
            "El NRC no puede estar vacío."
        );

        Optional<EducationalExperienceDTO> educationalExperience;

        try (Connection connection = DatabaseManager.getConnection();
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
            throws BusinessException, DataAccessException {
        List<EducationalExperienceDTO> educationalExperiences =
            new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
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

    public boolean existsSectionByPeriod(
            String schoolPeriod,
            String section
    ) throws BusinessException, DataAccessException {
        boolean exists;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(
                        EXISTS_SECTION_BY_PERIOD_QUERY
                    )) {
            statement.setString(
                1,
                schoolPeriod
            );
            statement.setString(
                2,
                section
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                exists =
                    resultSet.next()
                    && resultSet.getInt("total") > 0;
            }
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al validar seccion por periodo.",
                sqlException
            );

            throw new BusinessException(
                "Error al validar si la seccion ya existe en el periodo.",
                sqlException
            );
        }

        return exists;
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
        setOptionalEducationalExperienceData(
            educationalExperience,
            resultSet
        );

        return educationalExperience;
    }

    private void setOptionalEducationalExperienceData(
            EducationalExperienceDTO educationalExperience,
            ResultSet resultSet
    ) throws SQLException {
        Date startDate =
            resultSet.getDate("fecha_inicio");
        Date endDate =
            resultSet.getDate("fecha_fin");

        if (startDate != null) {
            educationalExperience.setStartDate(
                startDate.toLocalDate()
            );
        }

        if (endDate != null) {
            educationalExperience.setEndDate(
                endDate.toLocalDate()
            );
        }

        educationalExperience.setProfessorName(
            resultSet.getString("profesor_nombre")
        );
    }
}