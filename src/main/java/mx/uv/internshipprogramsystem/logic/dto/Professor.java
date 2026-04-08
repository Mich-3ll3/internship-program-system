package mx.uv.internshipprogramsystem.logic.dto;

public class Professor {
    private int staffNumber;
    private String names;
    private String paternalSurname;
    private String maternalSurname;
    private String email;
    private boolean isCoordinator;
    
    public Professor() {
        
    }

    public int getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getPaternalSurname() {
        return paternalSurname;
    }

    public void setPaternalSurname(String paternalSurname) {
        this.paternalSurname = paternalSurname;
    }

    public String getMaternalSurname() {
        return maternalSurname;
    }

    public void setMaternalSurname(String maternalSurname) {
        this.maternalSurname = maternalSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsCoordinator() {
        return isCoordinator;
    }

    public void setIsCoordinator(boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
    }
    
    @Override
    public String toString() {
        return names + " " + paternalSurname + " " + maternalSurname;
    }
}
