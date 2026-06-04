package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class EducationalExperienceInternDTO {

    private String nrc;
    private int internId;
    private LocalDate assignmentDate;
    private boolean countsOpportunity;
    private int opportunityNumber;
    private EducationalExperienceInternStatus status;

    public EducationalExperienceInternDTO(
            String nrc,
            int internId,
            LocalDate assignmentDate,
            boolean countsOpportunity,
            int opportunityNumber,
            EducationalExperienceInternStatus status
    ) {
        this.nrc = nrc;
        this.internId = internId;
        this.assignmentDate = assignmentDate;
        this.countsOpportunity = countsOpportunity;
        this.opportunityNumber = opportunityNumber;
        this.status = status;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public int getInternId() {
        return internId;
    }

    public void setInternId(int studentId) {
        this.internId = studentId;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(
            LocalDate assignmentDate
    ) {
        this.assignmentDate = assignmentDate;
    }

    public boolean getCountsOpportunity() {
        return countsOpportunity;
    }

    public void setCountsOpportunity(
            boolean countsOpportunity
    ) {
        this.countsOpportunity = countsOpportunity;
    }

    public int getOpportunityNumber() {
        return opportunityNumber;
    }

    public void setOpportunityNumber(
            int opportunityNumber
    ) {
        this.opportunityNumber = opportunityNumber;
    }

    public EducationalExperienceInternStatus getStatus() {
        return status;
    }

    public void setStatus(EducationalExperienceInternStatus status) {
        this.status = status;
    }
}