package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectApplicationDTO {
    
    private Integer id;
    private Integer internId;
    private Integer projectId;
    private Integer priority;
    
    private String projectName;
    private String organizationName;
    private String applicationDate;
    private String status;

    public ProjectApplicationDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInternId() {
        return internId;
    }

    public void setInternId(Integer internId) {
        this.internId = internId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getApplicationDate() {
        return this.applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}