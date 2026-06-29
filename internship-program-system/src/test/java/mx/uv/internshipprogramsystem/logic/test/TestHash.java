package mx.uv.internshipprogramsystem.logic.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;
import mx.uv.internshipprogramsystem.logic.security.SecurityManager;

public class TestHash {

    public static void main(String[] args) {
        SecurityManager securityManager = new SecurityManager();

        String correo = "mramos@uv.mx";
        String passwordPlana = "Profe#2026";
        String nombre = "Miguel";
        String apellidoP = "Ramos";
        String apellidoM = "Delgado";
        String numPersonal = "987654";

        String queryUsuario = "INSERT INTO USUARIO (correo_institucional, contrasena, nombre, apellido_paterno, apellido_materno, activo, rol) VALUES (?, ?, ?, ?, ?, TRUE, 'PROFESOR')";
        String queryProfesor = "INSERT INTO PROFESOR (usuario_id, numero_personal, es_coordinador) VALUES (?, ?, FALSE)";

        try (Connection conn = DataBaseManager.getConnection()) {
            
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUsuario = conn.prepareStatement(queryUsuario, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtProfesor = conn.prepareStatement(queryProfesor)) {

                System.out.println("Creando nuevo profesor desde Java...\n");

                String hashReal = securityManager.hashPassword(passwordPlana);

                stmtUsuario.setString(1, correo);
                stmtUsuario.setString(2, hashReal);
                stmtUsuario.setString(3, nombre);
                stmtUsuario.setString(4, apellidoP);
                stmtUsuario.setString(5, apellidoM);

                int affectedRows = stmtUsuario.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtUsuario.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int nuevoUsuarioId = generatedKeys.getInt(1);

                            stmtProfesor.setInt(1, nuevoUsuarioId);
                            stmtProfesor.setString(2, numPersonal);
                            stmtProfesor.executeUpdate();

                            conn.commit();
                            System.out.println("✅ ¡Éxito! Profesor " + nombre + " " + apellidoP + " creado correctamente en ambas tablas.");
                            System.out.println("Correo: " + correo);
                            System.out.println("Contraseña: " + passwordPlana);
                        }
                    }
                }
            } catch (Exception ex) {
                conn.rollback();
                System.err.println("❌ Error durante la inserción. Cambios revertidos.");
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            System.err.println("❌ Error de conexión a la base de datos:");
            e.printStackTrace();
        }
    }
}