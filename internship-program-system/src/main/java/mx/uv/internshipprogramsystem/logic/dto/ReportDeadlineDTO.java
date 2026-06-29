package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class ReportDeadlineDTO {
    private int professorId;
    private int reportNumber;
    private LocalDate deadline;

    public ReportDeadlineDTO() {}

    public ReportDeadlineDTO(int professorId, int reportNumber, LocalDate deadline) {
        this.professorId = professorId;
        this.reportNumber = reportNumber;
        this.deadline = deadline;
    }

    public int getProfessorId() { return professorId; }
    public void setProfessorId(int professorId) { this.professorId = professorId; }
    
    public int getReportNumber() { return reportNumber; }
    public void setReportNumber(int reportNumber) { this.reportNumber = reportNumber; }
    
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
}
