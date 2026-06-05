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
import mx.uv.internshipprogramsystem.logic.dto.PasswordRecoveryTokenDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.interfaces.IPasswordRecoveryTokenDAO;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class PasswordRecoveryTokenDAO implements IPasswordRecoveryTokenDAO {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(PasswordRecoveryTokenDAO.class);

    private static final String INSERT_PASSWORD_RECOVERY_TOKEN_QUERY =
        "INSERT INTO TOKEN_RECUPERACION "
        + "(usuario_id, token_hash, fecha_expiracion, usado) "
        + "VALUES (?, ?, ?, ?)";

    private static final String SELECT_PASSWORD_RECOVERY_TOKEN_BY_HASH_QUERY =
        "SELECT id, usuario_id, token_hash, fecha_expiracion, usado, fecha_uso "
        + "FROM TOKEN_RECUPERACION "
        + "WHERE token_hash = ?";

    private static final String MARK_PASSWORD_RECOVERY_TOKEN_AS_USED_QUERY =
        "UPDATE TOKEN_RECUPERACION "
        + "SET usado = true, fecha_uso = NOW() "
        + "WHERE id = ?";

    private static final String INVALIDATE_USER_PASSWORD_RECOVERY_TOKENS_QUERY =
        "UPDATE TOKEN_RECUPERACION "
        + "SET usado = true, fecha_uso = NOW() "
        + "WHERE usuario_id = ? AND usado = false";

    @Override
    public boolean create(
            PasswordRecoveryTokenDTO passwordRecoveryToken
    ) throws BusinessException {
        InputValidator.validateNotNull(
            passwordRecoveryToken,
            "El token de recuperación no puede ser nulo."
        );

        boolean wasCreated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement insertTokenStatement =
                 connection.prepareStatement(
                     INSERT_PASSWORD_RECOVERY_TOKEN_QUERY
                 )) {

            insertTokenStatement.setInt(
                1,
                passwordRecoveryToken.getUserId()
            );
            insertTokenStatement.setString(
                2,
                passwordRecoveryToken.getTokenHash()
            );
            insertTokenStatement.setTimestamp(
                3,
                passwordRecoveryToken.getExpirationDate()
            );
            insertTokenStatement.setBoolean(
                4,
                passwordRecoveryToken.isUsed()
            );

            wasCreated = insertTokenStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al crear token de recuperación",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al crear token de recuperación",
                sqlException
            );

            throw new BusinessException(
                "Error al crear el token de recuperación.",
                sqlException
            );
        }

        return wasCreated;
    }

    @Override
    public Optional<PasswordRecoveryTokenDTO> findByTokenHash(
            String tokenHash
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            tokenHash,
            "El hash del token de recuperación no puede estar vacío."
        );

        Optional<PasswordRecoveryTokenDTO> passwordRecoveryToken;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement selectTokenStatement =
                 connection.prepareStatement(
                     SELECT_PASSWORD_RECOVERY_TOKEN_BY_HASH_QUERY
                 )) {

            selectTokenStatement.setString(1, tokenHash);

            try (ResultSet resultSet = selectTokenStatement.executeQuery()) {
                passwordRecoveryToken =
                    buildOptionalPasswordRecoveryToken(resultSet);
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al buscar token de recuperación",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al buscar token de recuperación",
                sqlException
            );

            throw new BusinessException(
                "Error al buscar el token de recuperación.",
                sqlException
            );
        }

        return passwordRecoveryToken;
    }

    @Override
    public boolean markAsUsed(
            int tokenId
    ) throws BusinessException {
        validateTokenId(tokenId);

        boolean wasMarkedAsUsed;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateTokenStatement =
                 connection.prepareStatement(
                     MARK_PASSWORD_RECOVERY_TOKEN_AS_USED_QUERY
                 )) {

            updateTokenStatement.setInt(1, tokenId);

            wasMarkedAsUsed = updateTokenStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al marcar token de recuperación como usado",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al marcar token de recuperación como usado",
                sqlException
            );

            throw new BusinessException(
                "Error al marcar el token de recuperación como usado.",
                sqlException
            );
        }

        return wasMarkedAsUsed;
    }

    @Override
    public boolean invalidateTokensByUserId(
            int userId
    ) throws BusinessException {
        InputValidator.validatePositive(
            userId,
            "El identificador del usuario no es válido."
        );

        boolean wereTokensInvalidated;

        try (Connection connection = DataBaseManager.getConnection();
             PreparedStatement updateTokensStatement =
                 connection.prepareStatement(
                     INVALIDATE_USER_PASSWORD_RECOVERY_TOKENS_QUERY
                 )) {

            updateTokensStatement.setInt(1, userId);

            wereTokensInvalidated =
                updateTokensStatement.executeUpdate() > 0;
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión al invalidar tokens de recuperación",
                connectionException
            );

            throw new BusinessException(
                "No se pudo conectar con la base de datos.",
                connectionException
            );
        } catch (SQLException sqlException) {
            LOGGER.error(
                "Error SQL al invalidar tokens de recuperación del usuario {}",
                userId,
                sqlException
            );

            throw new BusinessException(
                "Error al invalidar los tokens de recuperación.",
                sqlException
            );
        }

        return wereTokensInvalidated;
    }

    private void validateTokenId(
            int tokenId
    ) throws BusinessException {
        if (tokenId <= 0) {
            throw new BusinessException(
                "El identificador del token no es válido."
            );
        }
    }

    private Optional<PasswordRecoveryTokenDTO> buildOptionalPasswordRecoveryToken(
            ResultSet resultSet
    ) throws SQLException {
        Optional<PasswordRecoveryTokenDTO> passwordRecoveryToken;

        if (resultSet.next()) {
            passwordRecoveryToken =
                Optional.of(buildPasswordRecoveryToken(resultSet));
        } else {
            passwordRecoveryToken = Optional.empty();
        }

        return passwordRecoveryToken;
    }

    private PasswordRecoveryTokenDTO buildPasswordRecoveryToken(
            ResultSet resultSet
    ) throws SQLException {
        PasswordRecoveryTokenDTO passwordRecoveryToken =
            new PasswordRecoveryTokenDTO();

        passwordRecoveryToken.setId(resultSet.getInt("id"));
        passwordRecoveryToken.setUserId(resultSet.getInt("usuario_id"));
        passwordRecoveryToken.setTokenHash(
            resultSet.getString("token_hash")
        );
        passwordRecoveryToken.setExpirationDate(
            resultSet.getTimestamp("fecha_expiracion")
        );
        passwordRecoveryToken.setUsed(resultSet.getBoolean("usado"));
        passwordRecoveryToken.setUsedDate(
            resultSet.getTimestamp("fecha_uso")
        );

        return passwordRecoveryToken;
    }
}