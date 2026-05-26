package mx.uv.internshipprogramsystem.logic.managers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.ProfessorValidator;
import mx.uv.internshipprogramsystem.logic.validations.UserValidator;

public class ProfessorRegistrationManager {
    private static final Logger LOGGER = 
        LoggerFactory.getLogger(ProfessorRegistrationManager.class);
    private final ActivationEmailManager activationEmailManager;

    public ProfessorRegistrationManager() {
        activationEmailManager = new ActivationEmailManager();
    }

    public boolean registerProfessor(UserDTO user, ProfessorDTO professor) throws BusinessException {
        boolean wasRegistered = false;
        String activationToken;

        validateRegisterProfessorData(user, professor);

        try (Connection connection = DataBaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                int userId = createUser(user, connection);

                ProfessorDTO registeredProfessor =
                    new ProfessorDTO(
                        professor.getStaffNumber(),
                        getCoordinatorValue(professor),
                        userId
                );

                createProfessor(registeredProfessor, connection);

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
                    "No se pudo completar el registro del profesor.",
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
                "Error SQL durante el registro transaccional del profesor",
                exception
            );

            throw new BusinessException(
                "No se pudo completar el registro del profesor.",
                exception
            );
        }

        return wasRegistered;
    }


    private void validateRegisterProfessorData(
        UserDTO user,
        ProfessorDTO professor
    ) throws BusinessException {
        UserValidator userValidator = new UserValidator();
        ProfessorValidator professorValidator = new ProfessorValidator();
        userValidator.validateUserForCreation(user);
        professorValidator.validateStaffNumber(professor.getStaffNumber());
    }

    private int createUser(UserDTO user, Connection connection) throws BusinessException {
        UserDAO userDAO = new UserDAO();
        int userId = userDAO.create(user, connection);
        return userId;
    }

    private void createProfessor(
        ProfessorDTO professor,
        Connection connection) 
    throws BusinessException {
        ProfessorDAO professorDAO = new ProfessorDAO();
        professorDAO.create(professor, connection);
    }

    private boolean getCoordinatorValue(ProfessorDTO professor) {
        boolean isCoordinator = false;

        if (professor.getIsCoordinator() != null) {
            isCoordinator = professor.getIsCoordinator();
        }

        return isCoordinator;
    }
}