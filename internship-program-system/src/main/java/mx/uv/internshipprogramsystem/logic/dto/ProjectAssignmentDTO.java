package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class ProjectAssignmentDTO {
    private int studentId;
    private int projectId;
    private int professorId;
    private LocalDate assignmentDate;

    public ProjectAssignmentDTO() {
    }

    public ProjectAssignmentDTO(int studentId, int projectId, int professorId, LocalDate assignmentDate) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.professorId = professorId;
        this.assignmentDate = assignmentDate;
    }

    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
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

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }
    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }
}
