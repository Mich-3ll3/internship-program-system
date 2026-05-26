package mx.uv.internshipprogramsystem.logic.managers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InternValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;



public class InternRegistrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternRegistrationManager.class);
    private final ActivationEmailManager activationEmailManager;

    public InternRegistrationManager() {
        activationEmailManager = new ActivationEmailManager();
    }

    public boolean registerIntern(UserDTO user, InternDTO intern) throws BusinessException {
        boolean wasRegistered = false;
        String activationToken;

        validateRegisterInternData(user, intern);

        try (Connection connection = DataBaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int userId = createUser(user, connection);

                InternDTO registeredIntern = new InternDTO(
                    intern.getEnrollmentNumber(),
                    userId
                );

                createIntern(registeredIntern, connection);

                activationToken =
                    activationEmailManager.createActivationToken(
                        userId,
                        connection
                    );

                connection.commit();

                activationEmailManager.sendActivationEmail(
                    user.getInstitutionalEmail(),
                    activationToken
                );

                wasRegistered = true;
            } catch (BusinessException exception) {
                connection.rollback();

                throw exception;
            } catch (SQLException exception) {
                connection.rollback();

                throw new BusinessException(
                    "No se pudo completar el registro del estudiante.",
                    exception
                );
            }
        } catch (SQLTransientConnectionException connectionException) {
            LOGGER.error(
                "Fallo de conexión con la base de datos",
                connectionException
            );

            throw new BusinessException(
                "No se pudo establecer conexión con la base de datos.",
                connectionException
            );
        } catch (SQLException exception) {
            LOGGER.error(
                "Error SQL durante el registro transaccional del estudiante",
                exception
            );

            throw new BusinessException(
                "No se pudo completar el registro del estudiante.",
                exception
            );
        }

        return wasRegistered;
    }


    private void validateRegisterInternData(
        UserDTO user,
        InternDTO intern
    ) throws BusinessException {
        UserValidator userValidator = new UserValidator();
        InternValidator internValidator = new InternValidator();
        userValidator.validateUserForCreation(user);
        internValidator.validateEnrollmentNumber(intern.getEnrollmentNumber());
    }

    private int createUser(UserDTO user, Connection connection) throws BusinessException {
        UserDAO userDAO = new UserDAO();
        int userId = userDAO.create(user, connection);
        return userId;
    }

    private void createIntern(InternDTO intern, Connection connection) throws BusinessException {
        InternDAO internDAO = new InternDAO();
        internDAO.create(intern, connection);
    }
}