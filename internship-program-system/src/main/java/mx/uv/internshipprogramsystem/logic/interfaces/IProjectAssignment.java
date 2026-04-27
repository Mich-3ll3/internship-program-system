package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ProjectAssignmentDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectAssignmentException;

public interface IProjectAssignment {
    boolean insert(ProjectAssignmentDTO assignment) throws ProjectAssignmentException;
    ProjectAssignmentDTO findById(int id) throws ProjectAssignmentException;
    boolean delete(int id) throws ProjectAssignmentException;
}
