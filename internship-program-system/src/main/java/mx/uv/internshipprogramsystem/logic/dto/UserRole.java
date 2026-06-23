package mx.uv.internshipprogramsystem.logic.dto;

public enum UserRole {
    ADMINISTRATOR("ADMINISTRADOR") {
        @Override
        public String getFxmlPath(UserDTO user) {
            return "AdminHomeDashboard.fxml";
        }
    },
    STUDENT("ESTUDIANTE") {
        @Override
        public String getFxmlPath(UserDTO user) {
            return "InternHomeDashboard.fxml";
        }
    },
    PROFESSOR("PROFESOR") {
        @Override
        public String getFxmlPath(UserDTO user) {
            if (user instanceof ProfessorDTO) {
                ProfessorDTO professor = (ProfessorDTO) user;
                if (professor.getIsCoordinator()) {
                    return "CoordinatorProfessorHomeDashboard.fxml";
                }
            }
            return "ProfessorHomeDashboard.fxml";
        }
    };

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

    public abstract String getFxmlPath(UserDTO user);
}
