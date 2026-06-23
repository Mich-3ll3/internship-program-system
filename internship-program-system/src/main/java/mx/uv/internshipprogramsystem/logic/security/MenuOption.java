package mx.uv.internshipprogramsystem.logic.security;

public class MenuOption {
    private String title;
    private String fxmlPath;
    private String iconPath;
    private Permission requiredPermission;

    public MenuOption() {
    }

    public MenuOption(String title, String fxmlPath, String iconPath,
            Permission requiredPermission) {
        this.title = title;
        this.fxmlPath = fxmlPath;
        this.iconPath = iconPath;
        this.requiredPermission = requiredPermission;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public void setFxmlPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Permission getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(Permission requiredPermission) {
        this.requiredPermission = requiredPermission;
    }
}
