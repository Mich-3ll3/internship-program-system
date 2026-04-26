package mx.uv.internshipprogramsystem.logic.dto;

public class InternDTO extends UserDTO{
    private String enrollmentNumber;

    public InternDTO() {
    }

    public InternDTO(String enrollmentNumber, String institucionalEmail, String password, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        super(institucionalEmail, password, name, firstSurname, secondSurname, isActive, rol);
        this.enrollmentNumber = enrollmentNumber;
    }

    public InternDTO(String enrollmentNumber, Integer id, String institucionalEmail, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        super(id, institucionalEmail, name, firstSurname, secondSurname, isActive, rol);
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }
    
}
