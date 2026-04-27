package mx.uv.internshipprogramsystem.logic.dto;

public class ProjectRequestDTO {
    private int studentId;
    private int projectId;
    private int priority;

    public ProjectRequestDTO() {
    }

    public ProjectRequestDTO(int studentId, int projectId, int priority) {
        this.studentId = studentId;
        this.projectId = projectId;
        this.priority = priority;
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

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
