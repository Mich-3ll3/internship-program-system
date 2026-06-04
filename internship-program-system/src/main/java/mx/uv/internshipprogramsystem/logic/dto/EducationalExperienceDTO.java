package mx.uv.internshipprogramsystem.logic.dto;

public class EducationalExperienceDTO {
    private String nrc;
    private String schoolPeriod;
    private String section;
    private int professorId;
    private boolean isActive;

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
    
    @Override
    public String toString() {
        return getNrc()
            + " - Sección "
            + getSection()
            + " - "
            + getSchoolPeriod();
    }
}