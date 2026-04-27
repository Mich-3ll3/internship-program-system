package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectAssignmentDTO {
    private int id;
    private int studentId;
    private int projectId;
    private String assignmentDate;

    public ProjectAssignmentDTO() {
    }

    public ProjectAssignmentDTO(int id, int studentId, int projectId, String assignmentDate) {
        this.id = id;
        this.studentId = studentId;
        this.projectId = projectId;
        this.assignmentDate = assignmentDate;
    }

    public ProjectAssignmentDTO(int studentId, int projectId, String assignmentDate) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.assignmentDate = assignmentDate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getAssignmentDate() {
        return assignmentDate;
    }
    public void setAssignmentDate(String assignmentDate) {
        this.assignmentDate = assignmentDate;
    }
}
