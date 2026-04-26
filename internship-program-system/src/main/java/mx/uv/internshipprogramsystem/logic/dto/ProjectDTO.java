package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectDTO {
    private Integer id;
    private String name;
    private String generalDescription;
    private String generalObjetive;
    private String immediateObjetives;
    private String mediateObjetive;
    private String methodology;
    private String resources;
    private String responsabilities;
    private Integer duration;
    private Integer linkedOrganizationId;
    private Integer projectResponsibleId;
    private Boolean isActive;

    public ProjectDTO() {
    }

    public ProjectDTO(String name, String generalDescription, String generalObjetive, String immediateObjetives, String mediateObjetive, String methodology, String resources, String responsabilities, Integer duration, Integer linkedOrganizationId, Integer projectResponsibleId, Boolean isActive) {
        this.name = name;
        this.generalDescription = generalDescription;
        this.generalObjetive = generalObjetive;
        this.immediateObjetives = immediateObjetives;
        this.mediateObjetive = mediateObjetive;
        this.methodology = methodology;
        this.resources = resources;
        this.responsabilities = responsabilities;
        this.duration = duration;
        this.linkedOrganizationId = linkedOrganizationId;
        this.projectResponsibleId = projectResponsibleId;
        this.isActive = isActive;
    }

    public ProjectDTO(Integer id, String name, String generalDescription, String generalObjetive, String immediateObjetives, String mediateObjetive, String methodology, String resources, String responsabilities, Integer duration, Integer linkedOrganizationId, Integer projectResponsibleId, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.generalDescription = generalDescription;
        this.generalObjetive = generalObjetive;
        this.immediateObjetives = immediateObjetives;
        this.mediateObjetive = mediateObjetive;
        this.methodology = methodology;
        this.resources = resources;
        this.responsabilities = responsabilities;
        this.duration = duration;
        this.linkedOrganizationId = linkedOrganizationId;
        this.projectResponsibleId = projectResponsibleId;
        this.isActive = isActive;
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

    public String getGeneralDescription() {
        return generalDescription;
    }

    public void setGeneralDescription(String generalDescription) {
        this.generalDescription = generalDescription;
    }

    public String getGeneralObjetive() {
        return generalObjetive;
    }

    public void setGeneralObjetive(String generalObjetive) {
        this.generalObjetive = generalObjetive;
    }

    public String getImmediateObjetives() {
        return immediateObjetives;
    }

    public void setImmediateObjetives(String immediateObjetives) {
        this.immediateObjetives = immediateObjetives;
    }

    public String getMediateObjetive() {
        return mediateObjetive;
    }

    public void setMediateObjetive(String mediateObjetive) {
        this.mediateObjetive = mediateObjetive;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getResponsabilities() {
        return responsabilities;
    }

    public void setResponsabilities(String responsabilities) {
        this.responsabilities = responsabilities;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getLinkedOrganizationId() {
        return linkedOrganizationId;
    }

    public void setLinkedOrganizationId(Integer linkedOrganizationId) {
        this.linkedOrganizationId = linkedOrganizationId;
    }

    public Integer getProjectResponsibleId() {
        return projectResponsibleId;
    }

    public void setProjectResponsibleId(Integer projectResponsibleId) {
        this.projectResponsibleId = projectResponsibleId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    
}
