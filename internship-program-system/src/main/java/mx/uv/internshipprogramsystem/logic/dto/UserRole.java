package mx.uv.internshipprogramsystem.logic.dto;

public enum UserRole {
    ADMINISTRATOR("ADMINISTRADOR"),
    STUDENT("ESTUDIANTE"),
    PROFESSOR("PROFESOR");

    private final String databaseValue;

    UserRole(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    public static UserRole fromDatabaseValue(String databaseValue) {
        for (UserRole role : values()) {
            if (role.databaseValue.equals(databaseValue)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Rol desconocido: " + databaseValue);
    }
}