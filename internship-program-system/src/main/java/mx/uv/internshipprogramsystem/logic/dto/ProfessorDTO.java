package mx.uv.internshipprogramsystem.logic.dto;

public class ProfessorDTO extends UserDTO {
    private String staffNumber;
    private Boolean isCoordinator;
    private int groups;
    private String currentEducationalExperience;
    private String educationalExperienceHistory;
    
    public ProfessorDTO() {
        
    }

    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            String institutionalEmail,
            String password,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(institutionalEmail, password, name, firstSurname, secondSurname, isActive, role);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }

    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive,
            UserRole role
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, role);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }
    
    public ProfessorDTO(
            String staffNumber,
            boolean isCoordinator,
            Integer id,
            String institutionalEmail,
            String name,
            String firstSurname,
            String secondSurname,
            Boolean isActive
    ) {
        super(id, institutionalEmail, name, firstSurname, secondSurname, isActive, null);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }

    public ProfessorDTO(String staffNumber, boolean isCoordinator, Integer id) {
        super(id);
        this.staffNumber = staffNumber;
        this.isCoordinator = isCoordinator;
    }
    
    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public Boolean getIsCoordinator() {
        return isCoordinator;
    }

    public void setIsCoordinator(Boolean isCoordinator) {
        this.isCoordinator = isCoordinator;
    }
    
    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
    }

    public String getCurrentEducationalExperience() {
        return currentEducationalExperience;
    }

    public void setCurrentEducationalExperience(
            String currentEducationalExperience
    ) {
        this.currentEducationalExperience = currentEducationalExperience;
    }

    public String getEducationalExperienceHistory() {
        return educationalExperienceHistory;
    }

    public void setEducationalExperienceHistory(
            String educationalExperienceHistory
    ) {
        this.educationalExperienceHistory = educationalExperienceHistory;
    }

    public String getCurrentEducationalExperienceDisplay() {
        String educationalExperienceDisplay =
            currentEducationalExperience;

        if (educationalExperienceDisplay == null
                || educationalExperienceDisplay.isBlank()) {
            educationalExperienceDisplay =
                "Sin NRC activo";
        } else {
            educationalExperienceDisplay =
                formatEducationalExperienceDisplay(
                    educationalExperienceDisplay
                );
        }

        return educationalExperienceDisplay;
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
                formatEducationalExperienceDisplay(
                    educationalExperienceHistoryDisplay
                );
        }

        return educationalExperienceHistoryDisplay;
    }

    private String formatEducationalExperienceDisplay(
            String educationalExperience
    ) {
        String[] experiences =
            educationalExperience.split("\\R|, ");
        StringBuilder formattedExperience =
            new StringBuilder();

        for (int index = 0; index < experiences.length; index++) {
            if (index > 0) {
                formattedExperience.append("\n");
            }

            formattedExperience.append(
                formatSingleEducationalExperience(
                    experiences[index]
                )
            );
        }

        return formattedExperience.toString();
    }

    private String formatSingleEducationalExperience(
            String educationalExperience
    ) {
        String formattedExperience =
            educationalExperience;
        String[] parts =
            educationalExperience.split(" - ");

        if (parts.length >= 3) {
            formattedExperience =
                "NRC " + parts[0]
                + " | " + parts[1]
                + " | " + parts[2].replace("Seccion ", "Sec. ");

            if (parts.length >= 4) {
                formattedExperience +=
                    " | " + parts[3];
            }
        }

        return formattedExperience;
    }

    public String getCoordinator() {
        return (getIsCoordinator() != null && getIsCoordinator()) ? "Si" : "No";
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
