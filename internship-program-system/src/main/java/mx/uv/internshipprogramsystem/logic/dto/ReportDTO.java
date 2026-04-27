package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class ReportDTO {
    private int id;
    private int number;
    private LocalDate date;
    private String generalObservations;
    private String type;
    private String status;
    private String filePath;
    private LocalDate reviewDate;
    private int studentId;
    private int professorId;
    private int projectId;

    public ReportDTO() {
    }

    public ReportDTO(int id, int number, LocalDate date, String generalObservations,
                     String type, String status, String filePath,
                     int studentId, int professorId, int projectId) {
        this.id = id;
        this.number = number;
        this.date = date;
        this.generalObservations = generalObservations;
        this.type = type;
        this.status = status;
        this.filePath = filePath;
        this.studentId = studentId;
        this.professorId = professorId;
        this.projectId = projectId;
    }

    public ReportDTO(int number, LocalDate date, String generalObservations,
                     String type, String status, String filePath,
                     int studentId, int professorId, int projectId) {
        this.number = number;
        this.date = date;
        this.generalObservations = generalObservations;
        this.type = type;
        this.status = status;
        this.filePath = filePath;
        this.studentId = studentId;
        this.professorId = professorId;
        this.projectId = projectId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getGeneralObservations() {
        return generalObservations;
    }
    public void setGeneralObservations(String generalObservations) {
        this.generalObservations = generalObservations;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getProfessorId() {
        return professorId;
    }
    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public int getProjectId() {
        return projectId;
    }
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
