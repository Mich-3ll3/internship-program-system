package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectActivityDTO {
    private Integer id;
    private String name;
    private String month;
    private Integer startWeek;
    private Integer endWeek;
    private Integer plannedHours;
    private Integer projectId;

    public ProjectActivityDTO() {
    }

    public ProjectActivityDTO(
            String name,
            String month,
            Integer startWeek,
            Integer endWeek,
            Integer plannedHours,
            Integer projectId
    ) {
        this.name = name;
        this.month = month;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.plannedHours = plannedHours;
        this.projectId = projectId;
    }

    public ProjectActivityDTO(
            Integer id,
            String name,
            String month,
            Integer startWeek,
            Integer endWeek,
            Integer plannedHours,
            Integer projectId
    ) {
        this.id = id;
        this.name = name;
        this.month = month;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.plannedHours = plannedHours;
        this.projectId = projectId;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
        this.endWeek = endWeek;
    }

    public Integer getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(Integer plannedHours) {
        this.plannedHours = plannedHours;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ProjectActivityDTO{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", month='" + month + '\''
                + ", startWeek=" + startWeek
                + ", endWeek=" + endWeek
                + ", plannedHours=" + plannedHours
                + ", projectId=" + projectId
                + '}';
    }
}
