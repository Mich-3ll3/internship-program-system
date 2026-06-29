package mx.uv.internshipprogramsystem.logic.security;

import java.util.ArrayList;
import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserRole;
import mx.uv.internshipprogramsystem.logic.managers.SessionManager;

public final class MenuAuthorityRegistry {
    private static final List<MenuOption> STATIC_OPTIONS = new ArrayList<>();

    static {
        STATIC_OPTIONS.add(new MenuOption(
            "Inicio",
            "",
            "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z",
            null
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Profesores",
            "/mx/uv/internshipprogramsystem/gui/fxml/ProfessorModuleDashboard.fxml",
            "M12 3L1 9l11 6 9-4.91V17h2V9L12 3z M5 13.18v4L12 21l7-3.82v-4L12 17L5 13.18z",
            Permission.CONSULT_PROFESSOR
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Organizaciones",
            "/mx/uv/internshipprogramsystem/gui/fxml/LinkedOrganizationManagementGUI.fxml",
            "M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5"
            + "h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8"
            + "v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z",
            Permission.CONSULT_ORGANIZATION
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Responsables",
            "/mx/uv/internshipprogramsystem/gui/fxml/ProjectResponsibleModuleDashboard.fxml",
            "M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2"
            + "L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zM10 4h4"
            + "v2h-4V4zm10 15H4V8h16v11z",
            Permission.CONSULT_PROJECT_RESPONSIBLE
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Proyectos",
            "/mx/uv/internshipprogramsystem/gui/fxml/ProjectsModuleDashboard.fxml",
            "M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2"
            + " 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H8V4h12v12z",
            Permission.CONSULT_PROJECT
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Actividades",
            "/mx/uv/internshipprogramsystem/gui/fxml/RegisterActivityDashboard.fxml",
            "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-9 14l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z",
            Permission.ADD_PROJECT_ACTIVITIES
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Alumnos",
            "/mx/uv/internshipprogramsystem/gui/fxml/InternModuleDashboard.fxml",
            "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0"
            + "-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z",
            Permission.CONSULT_INTERN
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Experiencias Educativas",
            "/mx/uv/internshipprogramsystem/gui/fxml/EducationalExperienceModuleDashboard.fxml",
            "M18 2H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1"
            + "-.9-2-2-2zM6 4h5v8l-2.5-1.5L6 12V4z",
            Permission.REGISTER_EDUCATIONAL_EXPERIENCE
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Reportes",
            "/mx/uv/internshipprogramsystem/gui/fxml/ReportHomeDashboard.fxml",
            "M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2"
            + " 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1"
            + " .45 1 1s-.55 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v"
            + "2zm0-4H7V7h10v2z",
            Permission.CONSULT_REPORT
        ));
        STATIC_OPTIONS.add(new MenuOption(
            "Documentos",
            "/mx/uv/internshipprogramsystem/gui/fxml/DocumentsHomeDashboard.fxml",
            "M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2"
            + "V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z",
            Permission.CONSULT_PROJECT
        ));
    }

    private MenuAuthorityRegistry() {
    }

    public static List<MenuOption> getAvailableOptions(UserRole role,
            List<Permission> userPermissions) {
        List<MenuOption> available = new ArrayList<>();
        UserDTO user = SessionManager.getInstance().getCurrentUser();
        for (MenuOption option : STATIC_OPTIONS) {
            Permission req = option.getRequiredPermission();
            if (req == null || userPermissions.contains(req)) {
                MenuOption copy = new MenuOption(
                    option.getTitle(),
                    option.getFxmlPath(),
                    option.getIconPath(),
                    option.getRequiredPermission()
                );
                if ("Inicio".equals(copy.getTitle()) && role != null && user != null) {
                    copy.setFxmlPath("/mx/uv/internshipprogramsystem/gui/fxml/" + role.getFxmlPath(user));
                }
                if ("Reportes".equals(copy.getTitle()) && role == UserRole.PROFESSOR) {
                    copy.setFxmlPath("/mx/uv/internshipprogramsystem/gui/fxml/ProfessorReportDashboard.fxml");
                }
                if ("Documentos".equals(copy.getTitle()) && role == UserRole.PROFESSOR) {
                    copy.setFxmlPath("/mx/uv/internshipprogramsystem/gui/fxml/ProfessorDocumentDashboard.fxml");
                }
                available.add(copy);
            }
        }
        return List.copyOf(available);
    }
}
