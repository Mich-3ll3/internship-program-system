package mx.uv.internshipprogramsystem.logic.interfaces;

import mx.uv.internshipprogramsystem.logic.dto.ProjectRequestDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ProjectRequestException;

import java.util.List;

public interface IProjectRequest {
    boolean insert(ProjectRequestDTO request) throws ProjectRequestException;
    List<ProjectRequestDTO> findByStudent(int studentId) throws ProjectRequestException;
    boolean delete(ProjectRequestDTO request) throws ProjectRequestException;
}
