package mx.uv.internshipprogramsystem.logic.dto;

import java.util.Optional;

public class PlannedActivityDTO {
    
    private int id;
    private String name;
    private int plannedHours;

    public PlannedActivityDTO() {
    }

    public PlannedActivityDTO(int id, String name, int plannedHours) {
        this.id = id;
        this.name = name;
        this.plannedHours = plannedHours;
    }

    public PlannedActivityDTO(String name, int plannedHours) {
        this.name = name;
        this.plannedHours = plannedHours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return Optional.ofNullable(name).orElse("");
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(int plannedHours) {
        this.plannedHours = plannedHours;
    }
}