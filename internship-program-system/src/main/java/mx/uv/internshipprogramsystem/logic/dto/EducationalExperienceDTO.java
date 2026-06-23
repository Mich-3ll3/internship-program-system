package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class EducationalExperienceDTO {
    private String nrc;
    private String schoolPeriod;
    private String section;
    private int professorId;
    private boolean isActive;
    private LocalDate startDate;
    private LocalDate endDate;
    private String professorName;

    public EducationalExperienceDTO (
            String nrc,
            String schoolPeriod,
            String section,
            int professorId,
            boolean isActive
    ) {
        this.nrc = nrc;
        this.schoolPeriod = schoolPeriod;
        this.section = section;
        this.professorId = professorId;
        this.isActive = isActive;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getSchoolPeriod() {
        return schoolPeriod;
    }

    public void setSchoolPeriod(String schoolPeriod) {
        this.schoolPeriod = schoolPeriod;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getProfessorName() {
        String name = professorName;

        if (name == null || name.isBlank()) {
            name = "Profesor " + professorId;
        }

        return name;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getStartDateDisplay() {
        return (startDate != null) ? startDate.toString() : "-";
    }

    public String getEndDateDisplay() {
        return (endDate != null) ? endDate.toString() : "-";
    }

    public String getDateRangeDisplay() {
        return getStartDateDisplay() + " - " + getEndDateDisplay();
    }

    public String getActiveStatus() {
        return getIsActive() ? "Activa" : "Inactiva";
    }
    
    @Override
    public String toString() {
        return getNrc()
            + " - Sección "
            + getSection()
            + " - "
            + getSchoolPeriod();
    }
}
