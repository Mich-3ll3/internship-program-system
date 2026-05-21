package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectDTO {
    private Integer id;
    private String name;
    private String generalDescription;
    private String generalObjective;
    private String immediateObjectives;
    private String mediateObjective;
    private String methodology;
    private String resources;
    private String responsibilities;
    private Integer duration;
    private Integer linkedOrganizationId;
    private Integer projectResponsibleId;
    private Boolean isActive;

    public ProjectDTO() {
    }

    public ProjectDTO(
            String name,
            String generalDescription,
            String generalObjective,
            String immediateObjectives,
            String mediateObjective,
            String methodology,
            String resources,
            String responsibilities,
            Integer duration,
            Integer linkedOrganizationId,
            Integer projectResponsibleId,
            Boolean isActive
    ) {
        this.name = name;
        this.generalDescription = generalDescription;
        this.generalObjective = generalObjective;
        this.immediateObjectives = immediateObjectives;
        this.mediateObjective = mediateObjective;
        this.methodology = methodology;
        this.resources = resources;
        this.responsibilities = responsibilities;
        this.duration = duration;
        this.linkedOrganizationId = linkedOrganizationId;
        this.projectResponsibleId = projectResponsibleId;
        this.isActive = isActive;
    }

    public ProjectDTO(
            Integer id,
            String name,
            String generalDescription,
            String generalObjective,
            String immediateObjectives,
            String mediateObjective,
            String methodology,
            String resources,
            String responsibilities,
            Integer duration,
            Integer linkedOrganizationId,
            Integer projectResponsibleId,
            Boolean isActive
    ) {
        this.id = id;
        this.name = name;
        this.generalDescription = generalDescription;
        this.generalObjective = generalObjective;
        this.immediateObjectives = immediateObjectives;
        this.mediateObjective = mediateObjective;
        this.methodology = methodology;
        this.resources = resources;
        this.responsibilities = responsibilities;
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

    public String getGeneralObjective() {
        return generalObjective;
    }

    public void setGeneralObjective(String generalObjective) {
        this.generalObjective = generalObjective;
    }

    public String getImmediateObjectives() {
        return immediateObjectives;
    }

    public void setImmediateObjectives(String immediateObjectives) {
        this.immediateObjectives = immediateObjectives;
    }

    public String getMediateObjective() {
        return mediateObjective;
    }

    public void setMediateObjective(String mediateObjective) {
        this.mediateObjective = mediateObjective;
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

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
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
