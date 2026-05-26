package mx.uv.internshipprogramsystem.logic.dto;

public class ProfessorDTO extends UserDTO {
    private String staffNumber;
    private Boolean isCoordinator;
    private int groups;
    
    public ProfessorDTO() {
        
    }

    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            String institutionalEmail,
            String password,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(institutionalEmail, password, name, firstSurname, secondSurname, isActive, role);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }

    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, role);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }
    
    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, null);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }

    public ProfessorDTO(String staffNumber, boolean isCoordinator, Integer id) {
        super(id);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }
    
    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public Boolean getIsCoordinator() {
        return isCoordinator;
    }

    public void setIsCoordinator(Boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
    }
    
    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }
    
    public String getCoordinator() {
        return (getIsCoordinator() != null && getIsCoordinator()) ? "Si" : "No";
    }
}
