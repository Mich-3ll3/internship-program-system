package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MonthlyReportContextDTO {
    
    private static final String DEFAULT_MAJOR = "Ingeniería de Software";
    private static final String NOT_AVAILABLE = "N/A";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String REPORT_NUMBER_FORMAT = "Reporte Mensual #%d";
    private static final String HOURS_FORMAT = "%d horas previas";

    private String nrc;
    private String professorName;
    private String schoolPeriod;
    private List<String> internNames;
    private String organizationName;
    private String projectName;
    private int accumulatedHours;
    private int reportNumber;
    private LocalDate reportDate;
    private String generalObjective;
    private String methodology;
    private List<ActivityPlanDTO> plannedActivities;
    private int projectId;
    private int professorId;

    public MonthlyReportContextDTO() {
        this.internNames = Collections.emptyList();
        this.plannedActivities = Collections.emptyList();
        this.reportDate = LocalDate.now();
    }

    public String getMajor() {
        return DEFAULT_MAJOR;
    }

    public String getNrc() {
        return Optional.ofNullable(nrc).orElse(NOT_AVAILABLE);
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getProfessorName() {
        return Optional.ofNullable(professorName).orElse(NOT_AVAILABLE);
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getSchoolPeriod() {
        return Optional.ofNullable(schoolPeriod).orElse(NOT_AVAILABLE);
    }

    public void setSchoolPeriod(String schoolPeriod) {
        this.schoolPeriod = schoolPeriod;
    }

    public List<String> getInternNames() {
        return internNames;
    }

    public String getFormattedInternNames() {
        if (internNames == null || internNames.isEmpty()) {
            return NOT_AVAILABLE;
        }
        return String.join(", ", internNames);
    }

    public void setInternNames(List<String> internNames) {
        if (internNames != null) {
            this.internNames = internNames;
        } else {
            this.internNames = Collections.emptyList();
        }
    }
    
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public String getOrganizationName() {
        return Optional.ofNullable(organizationName).orElse(NOT_AVAILABLE);
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProjectName() {
        return Optional.ofNullable(projectName).orElse(NOT_AVAILABLE);
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFormattedAccumulatedHours() {
        return String.format(HOURS_FORMAT, accumulatedHours);
    }

    public void setAccumulatedHours(int accumulatedHours) {
        this.accumulatedHours = accumulatedHours;
    }
    
    public int getAccumulatedHours() {
        return accumulatedHours;
    }

    public String getFormattedReportNumber() {
        return String.format(REPORT_NUMBER_FORMAT, reportNumber);
    }

    public String getFormattedReportDate() {
        if (reportDate == null) {
            return NOT_AVAILABLE;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return reportDate.format(formatter);
    }
    
    public int getReportNumber() {
        return reportNumber;
    }
    
    public void setReportNumber(int reportNumber) {
        this.reportNumber = reportNumber;
    }

    public String getGeneralObjective() {
        return Optional.ofNullable(generalObjective).orElse(NOT_AVAILABLE);
    }

    public void setGeneralObjective(String generalObjective) {
        this.generalObjective = generalObjective;
    }

    public String getMethodology() {
        return Optional.ofNullable(methodology).orElse(NOT_AVAILABLE);
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public List<ActivityPlanDTO> getPlannedActivities() {
        return plannedActivities;
    }

    public void setPlannedActivities(List<ActivityPlanDTO> plannedActivities) {
        if (plannedActivities != null) {
            this.plannedActivities = plannedActivities;
        } else {
            this.plannedActivities = Collections.emptyList();
        }
    }
}