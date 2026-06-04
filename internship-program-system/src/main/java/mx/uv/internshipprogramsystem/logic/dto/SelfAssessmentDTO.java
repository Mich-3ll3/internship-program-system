package mx.uv.internshipprogramsystem.logic.dto;

import java.time.LocalDate;

public class SelfAssessmentDTO {

    private int id;

    private int studentId;
    private int projectId;
    private int organizationId;
    private int responsibleId;

    private String studentName;
    private String projectName;
    private String responsibleName;
    private String organizationName;

    private String department;
    private String place;
    private LocalDate date;

    private int afirmacion1;
    private int afirmacion2;
    private int afirmacion3;
    private int afirmacion4;
    private int afirmacion5;
    private int afirmacion6;
    private int afirmacion7;
    private int afirmacion8;
    private int afirmacion9;
    private int afirmacion10;

    private String observations;

    public SelfAssessmentDTO() {}


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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }


    public int getProjectId() {
        return projectId;
    }
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getResponsibleId() {
        return responsibleId;
    }
    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public String getStudentName() {
        return studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getResponsibleName() {
        return responsibleName;
    }
    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAfirmacion1() {
        return afirmacion1;
    }
    public void setAfirmacion1(int afirmacion1) {
        this.afirmacion1 = afirmacion1;
    }

    public int getAfirmacion2() {
        return afirmacion2;
    }
    public void setAfirmacion2(int afirmacion2) {
        this.afirmacion2 = afirmacion2;
    }

    public int getAfirmacion3() {
        return afirmacion3;
    }
    public void setAfirmacion3(int afirmacion3) {
        this.afirmacion3 = afirmacion3;
    }

    public int getAfirmacion4() {
        return afirmacion4;
    }
    public void setAfirmacion4(int afirmacion4) {
        this.afirmacion4 = afirmacion4;
    }

    public int getAfirmacion5() {
        return afirmacion5;
    }
    public void setAfirmacion5(int afirmacion5) {
        this.afirmacion5 = afirmacion5;
    }

    public int getAfirmacion6() {
        return afirmacion6;
    }
    public void setAfirmacion6(int afirmacion6) {
        this.afirmacion6 = afirmacion6;
    }

    public int getAfirmacion7() {
        return afirmacion7;
    }
    public void setAfirmacion7(int afirmacion7) {
        this.afirmacion7 = afirmacion7;
    }

    public int getAfirmacion8() {
        return afirmacion8;
    }
    public void setAfirmacion8(int afirmacion8) {
        this.afirmacion8 = afirmacion8;
    }

    public int getAfirmacion9() {
        return afirmacion9;
    }
    public void setAfirmacion9(int afirmacion9) {
        this.afirmacion9 = afirmacion9;
    }

    public int getAfirmacion10() {
        return afirmacion10;
    }
    public void setAfirmacion10(int afirmacion10) {
        this.afirmacion10 = afirmacion10;
    }

    public String getObservations() {
        return observations;
    }
    public void setObservations(String observations) {
        this.observations = observations;
    }
}
