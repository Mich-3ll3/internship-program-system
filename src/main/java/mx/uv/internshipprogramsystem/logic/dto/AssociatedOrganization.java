package mx.uv.internshipprogramsystem.logic.dto;

public class AssociatedOrganization {
    private Integer associatedOrganizationId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String email;
    private String phoneNumber;
    private String sector;
    private Integer indirectUserCount;
    private Integer directUserCount;
    
    public AssociatedOrganization() {
    
    }

    public Integer getAssociatedOrganizationId() {
        return associatedOrganizationId;
    }

    public void setAssociatedOrganizationId(Integer associatedOrganizationId) {
        this.associatedOrganizationId = associatedOrganizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Integer getIndirectUserCount() {
        return indirectUserCount;
    }

    public void setIndirectUserCount(Integer indirectUserCount) {
        this.indirectUserCount = indirectUserCount;
    }

    public Integer getDirectUserCount() {
        return directUserCount;
    }

    public void setDirectUserCount(Integer directUserCount) {
        this.directUserCount = directUserCount;
    }

}
