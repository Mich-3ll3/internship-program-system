package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalTime;

public class ProjectScheduleDTO {
    private Integer id;
    private String weekDay;
    private LocalTime entryTime;
    private LocalTime exitTime;
    private Integer projectId;

    public ProjectScheduleDTO() {
    }

    public ProjectScheduleDTO(
            String weekDay,
            LocalTime entryTime,
            LocalTime exitTime,
            Integer projectId
    ) {
        this.weekDay = weekDay;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.projectId = projectId;
    }

    public ProjectScheduleDTO(
            Integer id,
            String weekDay,
            LocalTime entryTime,
            LocalTime exitTime,
            Integer projectId
    ) {
        this.id = id;
        this.weekDay = weekDay;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.projectId = projectId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ProjectScheduleDTO{"
                + "id=" + id
                + ", weekDay='" + weekDay + '\''
                + ", entryTime=" + entryTime
                + ", exitTime=" + exitTime
                + ", projectId=" + projectId
                + '}';
    }
}