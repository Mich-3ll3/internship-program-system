package mx.uv.internshipprogramsystem.dataaccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseManager {
    private static String url;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream input = DataBaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties en el classpath");
            }
            props.load(input);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
