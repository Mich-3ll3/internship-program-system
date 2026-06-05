package mx.uv.internshipprogramsystem.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivityPlanDTO {

    private final StringProperty activityName = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("Real");
    private final StringProperty week1Hours = new SimpleStringProperty("0");
    private final StringProperty week2Hours = new SimpleStringProperty("0");
    private final StringProperty week3Hours = new SimpleStringProperty("0");
    private final StringProperty week4Hours = new SimpleStringProperty("0");

    // --- Getters para el TableView ---
    public String getActivityName() {
        return activityName.get();
    }

    public String getType() {
        return type.get();
    }

    public String getWeek1Hours() {
        return week1Hours.get();
    }

    public String getWeek2Hours() {
        return week2Hours.get();
    }

    public String getWeek3Hours() {
        return week3Hours.get();
    }

    public String getWeek4Hours() {
        return week4Hours.get();
    }

    // --- Setters ---
    public void setActivityName(String name) {
        this.activityName.set(name);
    }

    public void setWeek1Hours(String h) {
        this.week1Hours.set(h);
    }

    public void setWeek2Hours(String h) {
        this.week2Hours.set(h);
    }

    public void setWeek3Hours(String h) {
        this.week3Hours.set(h);
    }

    public void setWeek4Hours(String h) {
        this.week4Hours.set(h);
    }

    // --- Property Getters (NECESARIOS para el CellValueFactory) ---
    public StringProperty activityNameProperty() {
        return activityName;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty week1HoursProperty() {
        return week1Hours;
    }

    public StringProperty week2HoursProperty() {
        return week2Hours;
    }

    public StringProperty week3HoursProperty() {
        return week3Hours;
    }

    public StringProperty week4HoursProperty() {
        return week4Hours;
    }

    public String getTimeType() {
        return type.get();
    }
}