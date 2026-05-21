package mx.uv.internshipprogramsystem.logic.dto;

public class UserDTO {
    private Integer id;
    private String institutionalEmail;
    private String password;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private Boolean isActive;
    private UserRole role;

    public UserDTO() {
    }

    public UserDTO(
            String institutionalEmail,
            String password,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        this.institutionalEmail = institutionalEmail;
        this.password = password;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.isActive = isActive;
        this.role = role;
    }

    public UserDTO(
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        this.id = id;
        this.institutionalEmail = institutionalEmail;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = (secondSurname != null ? secondSurname : "");
        this.isActive = isActive;
        this.role = role;
    }

    public UserDTO(Integer id) {
        this.id = id;
    }
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = (secondSurname != null) ? secondSurname : "";
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getFullName() {
        String firstName = (name != null) ? name : "";
        String firstLastName = (firstSurname != null) ? firstSurname : "";
        String secondLastName = "";
        if (secondSurname != null && !secondSurname.equalsIgnoreCase("null")) {
            secondLastName = secondSurname;
        }

        return (firstName + " " + firstLastName + " " + secondLastName)
            .trim()
            .replaceAll("\\s+", " ");
    }

    public String getActive() {
        return getIsActive() != null && getIsActive() ? "Activo" : "Inactivo";
    }
}
