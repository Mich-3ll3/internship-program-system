package mx.uv.internshipprogramsystem.logic.dto;

public class ProfessorDTO extends UserDTO{
    private int staffNumber;
    private boolean isCoordinator;
    
    public ProfessorDTO() {
        
    }

    public ProfessorDTO(int staffNumber, boolean isCoordinator, String institucionalEmail, String password, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        super(institucionalEmail, password, name, firstSurname, secondSurname, isActive, rol);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }

    public ProfessorDTO(int staffNumber, boolean isCoordinator, Integer id, String institucionalEmail, String name, String firstSurname, String secondSurname, Boolean isActive, RolUsuario rol) {
        super(id, institucionalEmail, name, firstSurname, secondSurname, isActive, rol);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }
    
    public ProfessorDTO(int staffNumber, boolean isCoordinator, Integer id, String institucionalEmail, String name, String firstSurname, String secondSurname, Boolean isActive) {
        super(id, institucionalEmail, name, firstSurname, secondSurname, isActive, null);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }


    public int getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    public boolean getIsCoordinator() {
        return isCoordinator;
    }

    public void setIsCoordinator(boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
    }
}