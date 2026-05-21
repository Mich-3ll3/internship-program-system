package mx.uv.internshipprogramsystem.logic.dto;

public class InternDTO extends UserDTO {
    private String enrollmentNumber;
    private String nrc;

    public InternDTO() {
    }

    public InternDTO(
            String enrollmentNumber,
            String institutionalEmail,
            String password,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(institutionalEmail, password, name, firstSurname, secondSurname, isActive, role);
        this.enrollmentNumber = enrollmentNumber;
    }

    public InternDTO(
            String enrollmentNumber,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, role);
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public InternDTO(
            String enrollmentNumber,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, null);
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public InternDTO(String enrollmentNumber, Integer id) {
        super(id);
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = (nrc != null) ? nrc : "N/A";
    }
}
