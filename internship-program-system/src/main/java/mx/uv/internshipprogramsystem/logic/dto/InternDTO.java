package mx.uv.internshipprogramsystem.logic.dto;

public class InternDTO extends UserDTO {
    
    private static final String DEFAULT_NRC = "N/A";
    private static final String DEFAULT_EXPERIENCE_STATUS =
        "Sin experiencia";
    
    private String enrollmentNumber;
    private String nrc;
    private EducationalExperienceInternStatus educationalExperienceStatus;
    private String educationalExperienceHistory;

    public InternDTO() {
    }

    public InternDTO(
            String enrollmentNumber,
            String institutionalEmail,
            String password,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(institutionalEmail, password, name, firstSurname, secondSurname, isActive, role);
        this.enrollmentNumber = enrollmentNumber;
    }

    public InternDTO(
            String enrollmentNumber,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, role);
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public InternDTO(
            String enrollmentNumber,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, null);
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public InternDTO(String enrollmentNumber, Integer id) {
        super(id);
        this.enrollmentNumber = enrollmentNumber;
    }

    public String getEnrollmentNumber() {
        return enrollmentNumber;
    }

    public void setEnrollmentNumber(String enrollmentNumber) {
        this.enrollmentNumber = enrollmentNumber;
    }
    
    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = (nrc != null) ? nrc : DEFAULT_NRC;
    }

    public EducationalExperienceInternStatus
            getEducationalExperienceStatus() {
        return educationalExperienceStatus;
    }

    public void setEducationalExperienceStatus(
            EducationalExperienceInternStatus educationalExperienceStatus
    ) {
        this.educationalExperienceStatus =
            educationalExperienceStatus;
    }

    public String getEducationalExperienceHistory() {
        return educationalExperienceHistory;
    }

    public void setEducationalExperienceHistory(
            String educationalExperienceHistory
    ) {
        this.educationalExperienceHistory =
            educationalExperienceHistory;
    }

    public String getEducationalExperienceStatusDisplayName() {
        String statusDisplayName =
            DEFAULT_EXPERIENCE_STATUS;

        if (educationalExperienceStatus != null) {
            statusDisplayName =
                educationalExperienceStatus.getDisplayName();
        }

        return statusDisplayName;
    }

    public boolean hasActiveEducationalExperience() {
        return EducationalExperienceInternStatus.ACTIVA.equals(
            educationalExperienceStatus
        );
    }

    public String getCurrentEducationalExperienceDisplay() {
        String currentEducationalExperience =
            "Sin NRC activo";

        if (hasActiveEducationalExperience()) {
            currentEducationalExperience =
                "NRC " + nrc + " | "
                + getEducationalExperienceStatusDisplayName();
        }

        return currentEducationalExperience;
    }

    public String getEducationalExperienceHistoryDisplay() {
        String educationalExperienceHistoryDisplay =
            educationalExperienceHistory;

        if (educationalExperienceHistoryDisplay == null
                || educationalExperienceHistoryDisplay.isBlank()) {
            educationalExperienceHistoryDisplay =
                "Sin NRC registrados";
        } else {
            educationalExperienceHistoryDisplay =
                educationalExperienceHistory.replace(", ", "\n");
        }

        return educationalExperienceHistoryDisplay;
    }
}
