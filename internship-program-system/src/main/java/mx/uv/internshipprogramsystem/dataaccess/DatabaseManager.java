package mx.uv.internshipprogramsystem.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

public final class DatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private static final DatabaseConfiguration DATABASE_CONFIGURATION = DatabaseConfiguration.load();

    private DatabaseManager() {
    }

    public static Connection getConnection() throws DataAccessException {

        try {

            return DriverManager.getConnection(
                    DATABASE_CONFIGURATION.getUrl(),
                    DATABASE_CONFIGURATION.getUser(),
                    DATABASE_CONFIGURATION.getPassword()
            );

        } catch (SQLException exception) {
            LOGGER.error("No fue posible establecer conexi├│n con la base de datos.",exception);
            throw new DataAccessException("No fue posible establecer conexi├│n con la base de datos.",exception);
        }
    }
}
