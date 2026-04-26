package mx.uv.internshipprogramsystem.logic.dto;

public class UserDTO {
    private Integer id;
    private String institucionalEmail;
    private String password;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private Boolean isActive;
    private RolUsuario rol;

    public UserDTO() {
    }

    public UserDTO(String institucionalEmail, String password, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        this.institucionalEmail = institucionalEmail;
        this.password = password;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.isActive = isActive;
        this.rol = rol;
    }

    public UserDTO(Integer id, String institucionalEmail, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        this.id = id;
        this.institucionalEmail = institucionalEmail;
        this.name = name;
        this.firstSurname = firstSurname;
        this.secondSurname = secondSurname;
        this.isActive = isActive;
        this.rol = rol;
    }
        
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInstitucionalEmail() {
        return institucionalEmail;
    }

    public void setInstitucionalEmail(String institucionalEmail) {
        this.institucionalEmail = institucionalEmail;
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
        this.secondSurname = secondSurname;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
