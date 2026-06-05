package mx.uv.internshipprogramsystem.logic.dto;

import java.util.Optional;

public class PlannedActivityDTO {

    private String name;
    private int plannedHours;

    public PlannedActivityDTO() {
    }

    public PlannedActivityDTO(String name, int plannedHours) {
        this.name = name;
        this.plannedHours = plannedHours;
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