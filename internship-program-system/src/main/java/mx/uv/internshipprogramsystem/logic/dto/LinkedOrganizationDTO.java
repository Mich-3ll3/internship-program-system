package mx.uv.internshipprogramsystem.logic.dto;

public class LinkedOrganizationDTO {
    private Integer id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String email;
    private String phoneNumber;
    private String sector;
    private Integer indirectUserCount;
    private Integer directUserCount;
    
    public LinkedOrganizationDTO() {
    
    }

    public LinkedOrganizationDTO(String name, String address, String city, String state, String email, String phoneNumber, String sector, Integer indirectUserCount, Integer directUserCount) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.sector = sector;
        this.indirectUserCount = indirectUserCount;
        this.directUserCount = directUserCount;
    }

    public LinkedOrganizationDTO(Integer id, String name, String address, String city, String state, String email, String phoneNumber, String sector, Integer indirectUserCount, Integer directUserCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.sector = sector;
        this.indirectUserCount = indirectUserCount;
        this.directUserCount = directUserCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
