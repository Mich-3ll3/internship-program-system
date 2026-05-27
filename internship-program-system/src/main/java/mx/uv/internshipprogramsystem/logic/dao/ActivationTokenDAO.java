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
import mx.uv.internshipprogramsystem.logic.dto.ActivationTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IActivationTokenDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ActivationTokenDAO implements IActivationTokenDAO {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ActivationTokenDAO.class);

    private static final String INSERT_ACTIVATION_TOKEN_QUERY =
        "INSERT INTO TOKEN_ACTIVACION "
        + "(usuario_id, token_hash, fecha_expiracion, usado) "
        + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_ACTIVATION_TOKEN_BY_HASH_QUERY =
        "SELECT id, usuario_id, token_hash, fecha_expiracion, usado, fecha_uso "
        + "FROM TOKEN_ACTIVACION "
        + "WHERE token_hash = ?";

    private static final String MARK_ACTIVATION_TOKEN_AS_USED_QUERY =
        "UPDATE TOKEN_ACTIVACION SET usado = true, fecha_uso = NOW() "
        + "WHERE id = ?";

    private static final String INVALIDATE_USER_TOKENS_QUERY =
        "UPDATE TOKEN_ACTIVACION "
        + "SET usado = true, fecha_uso = NOW() "
        + "WHERE usuario_id = ? AND usado = false";

    @Override
    public boolean create(ActivationTokenDTO activationToken) throws BusinessException {
        InputValidator.validateNotNull(
        activationToken,
        "El token de activación no puede ser nulo."
        );

        boolean wasCreated;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement insertActivationTokenStatement =
                connection.prepareStatement(INSERT_ACTIVATION_TOKEN_QUERY)) {
            insertActivationTokenStatement.setInt(1, activationToken.getUserId());
            insertActivationTokenStatement.setString(2, activationToken.getTokenHash());
            insertActivationTokenStatement.setTimestamp(3,activationToken.getExpirationDate());
            insertActivationTokenStatement.setBoolean(4,activationToken.isUsed());

            wasCreated = insertActivationTokenStatement.executeUpdate() > 0;
            
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error("Fallo de conexión con la base de datos", connectionException);
            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error("Error SQL al crear token de activación", sqlException);
            throw new BusinessException("Error al crear el token de activación.", sqlException);
        }
        return wasCreated;
    }

    public boolean create(
        ActivationTokenDTO activationToken,
        Connection connection
        ) throws BusinessException {
            InputValidator.validateNotNull(
                activationToken,
                "El token de activación no puede ser nulo."
            );

            InputValidator.validateNotNull(
                connection,
                "La conexión no puede ser nula."
            );

            boolean wasCreated;

            try (PreparedStatement insertActivationTokenStatement =
                    connection.prepareStatement(INSERT_ACTIVATION_TOKEN_QUERY)) {
                insertActivationTokenStatement.setInt(
                    1,
                    activationToken.getUserId()
                );
                insertActivationTokenStatement.setString(
                    2,
                    activationToken.getTokenHash()
                );
                insertActivationTokenStatement.setTimestamp(
                    3,
                    activationToken.getExpirationDate()
                );
                insertActivationTokenStatement.setBoolean(
                    4,
                    activationToken.isUsed()
                );

                wasCreated = insertActivationTokenStatement.executeUpdate() > 0;
            } catch (SQLException sqlException) {
                throw new BusinessException(
                    "Error al crear el token de activación.",
                    sqlException
                );
            }

            return wasCreated;
        }

    @Override
    public Optional<ActivationTokenDTO> findByTokenHash(String tokenHash)
            throws BusinessException {
        InputValidator.validateNotEmpty(
            tokenHash,
            "El hash del token no puede estar vacío."
        );

        Optional<ActivationTokenDTO> activationToken;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectActivationTokenStatement =
                 connection.prepareStatement(
                     SELECT_ACTIVATION_TOKEN_BY_HASH_QUERY
                 )) {
            selectActivationTokenStatement.setString(1, tokenHash);

            try (ResultSet resultSet =
                    selectActivationTokenStatement.executeQuery()) {
                activationToken = buildOptionalActivationToken(resultSet);
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
                "Error SQL al buscar token de activación",
                sqlException
            );

            throw new BusinessException(
                "Error al buscar el token de activación.",
                sqlException
            );
        }

        return activationToken;
    }

    @Override
    public boolean markAsUsed(int tokenId) throws BusinessException {
        validateTokenId(tokenId);

        boolean wasMarkedAsUsed;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement markActivationTokenStatement =
                 connection.prepareStatement(
                     MARK_ACTIVATION_TOKEN_AS_USED_QUERY
                 )) {
            markActivationTokenStatement.setInt(1, tokenId);

            wasMarkedAsUsed =
                markActivationTokenStatement.executeUpdate() > 0;
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
                "Error SQL al marcar token como usado",
                sqlException
            );

            throw new BusinessException(
                "Error al marcar el token como usado.",
                sqlException
            );
        }

        return wasMarkedAsUsed;
    }

    public boolean invalidateTokensByUserId(int userId)
        throws BusinessException {
        InputValidator.validatePositive(
            userId,
            "El identificador del usuario no es válido."
        );

        boolean wereTokensInvalidated;

        try (Connection connection = DataBaseManager.getConnection();
            PreparedStatement invalidateTokensStatement =
                connection.prepareStatement(
                    INVALIDATE_USER_TOKENS_QUERY
                )) {

            invalidateTokensStatement.setInt(1, userId);

            wereTokensInvalidated =
                invalidateTokensStatement.executeUpdate() > 0;

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
                "Error SQL al invalidar tokens del usuario {}",
                userId,
                sqlException
            );

            throw new BusinessException(
                "Error al invalidar los tokens de activación.",
                sqlException
            );
        }

        return wereTokensInvalidated;
    }

    private void validateTokenId(int tokenId) throws BusinessException {
        if (tokenId <= 0) {
            throw new BusinessException(
                "El identificador del token no es válido."
            );
        }
    }

    private Optional<ActivationTokenDTO> buildOptionalActivationToken(
            ResultSet resultSet
    ) throws SQLException {
        Optional<ActivationTokenDTO> activationToken;

        if (resultSet.next()) {
            activationToken =
                Optional.of(buildActivationToken(resultSet));
        } else {
            activationToken = Optional.empty();
        }

        return activationToken;
    }

    private ActivationTokenDTO buildActivationToken(ResultSet resultSet)
            throws SQLException {
        ActivationTokenDTO activationToken = new ActivationTokenDTO();

        activationToken.setId(resultSet.getInt("id"));
        activationToken.setUserId(resultSet.getInt("usuario_id"));
        activationToken.setTokenHash(resultSet.getString("token_hash"));
        activationToken.setExpirationDate(
            resultSet.getTimestamp("fecha_expiracion")
        );
        activationToken.setUsed(resultSet.getBoolean("usado"));
        activationToken.setUsedDate(resultSet.getTimestamp("fecha_uso"));

        return activationToken;
    }
}