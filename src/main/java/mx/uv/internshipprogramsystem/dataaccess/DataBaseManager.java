package mx.uv.internshipprogramsystem.dataaccess;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseManager {
    private String url;
    private String user;
    private String password;

    public DataBaseManager() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
