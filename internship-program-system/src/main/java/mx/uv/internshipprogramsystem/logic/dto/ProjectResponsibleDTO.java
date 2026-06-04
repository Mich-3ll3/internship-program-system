package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectResponsibleDTO {
    private int id;
    private String firstName;
    private String lastNameFather;
    private String lastNameMother;
    private String email;
    private String position;
    private int organizationId;
    private String organizationName;
    private String projectName;

    public ProjectResponsibleDTO() {
    }

    public ProjectResponsibleDTO(
            int id,
            String firstName,
            String lastNameFather,
            String lastNameMother,
            String email,
            String position,
            int organizationId
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.email = email;
        this.position = position;
        this.organizationId = organizationId;
    }

    public ProjectResponsibleDTO(
            String firstName,
            String lastNameFather,
            String lastNameMother,
            String email,
            String position,
            int organizationId
    ) {
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.email = email;
        this.position = position;
        this.organizationId = organizationId;
    }
    
    public ProjectResponsibleDTO(
        int id,
        String firstName,
        String lastNameFather,
        String lastNameMother,
        String email,
        String position,
        int organizationId,
        String organizationName,
        String projectName
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastNameFather = lastNameFather;
        this.lastNameMother = lastNameMother;
        this.email = email;
        this.position = position;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.projectName = projectName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNameFather() {
        return lastNameFather;
    }
    public void setLastNameFather(String lastNameFather) {
        this.lastNameFather = lastNameFather;
    }

    public String getLastNameMother() {
        return lastNameMother;
    }
    public void setLastNameMother(String lastNameMother) {
        this.lastNameMother = lastNameMother;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public int getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getFullName() {
        String fullName =
            firstName + " " + lastNameFather;

        if (
            lastNameMother != null
            && !lastNameMother.isBlank()
        ) {
            fullName =
                fullName + " "
                + lastNameMother;
        }

        return fullName.trim();
    }
}
