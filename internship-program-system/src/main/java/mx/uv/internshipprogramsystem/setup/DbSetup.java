package mx.uv.internshipprogramsystem.setup;

import java.sql.Connection;
import java.sql.Statement;
import mx.uv.internshipprogramsystem.dataaccess.DatabaseManager;

public class DbSetup {
    public static void main(String[] args) {
        try (Connection c = DatabaseManager.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS NOTIFICACION (" +
                      "id INT AUTO_INCREMENT PRIMARY KEY, " +
                      "usuario_id INT NOT NULL, " +
                      "mensaje TEXT NOT NULL, " +
                      "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                      "leida BOOLEAN DEFAULT FALSE)");
                      
            s.execute("CREATE TABLE IF NOT EXISTS CONFIGURACION_REPORTE (" +
                      "profesor_id INT NOT NULL, " +
                      "numero_reporte INT NOT NULL, " +
                      "fecha_limite DATE NOT NULL, " +
                      "PRIMARY KEY (profesor_id, numero_reporte))");
            System.out.println("Tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
