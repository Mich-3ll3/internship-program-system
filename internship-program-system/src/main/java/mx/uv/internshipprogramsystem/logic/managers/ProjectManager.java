package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dao.ProjectDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import mx.uv.internshipprogramsystem.logic.interfaces.IProjectDAO;
import mx.uv.internshipprogramsystem.logic.validations.ProjectValidator;

public class ProjectManager {
    private final IProjectDAO projectDAO;

    public ProjectManager() {
        projectDAO = new ProjectDAO();
    }

    public boolean updateProject(ProjectDTO project)
            throws BusinessException {
        boolean wasUpdated;

        try {
            ProjectValidator.validateForUpdate(project);

            wasUpdated = projectDAO.update(project);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }

        return wasUpdated;
    }

    public boolean deactivateProject(int projectId)
            throws BusinessException {
        boolean wasDeactivated;

        try {
            ProjectValidator.validateProjectId(projectId);

            wasDeactivated = projectDAO.deactivate(projectId);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }

        return wasDeactivated;
    }

    public Optional<ProjectDTO> findProjectById(int projectId)
            throws BusinessException {
        Optional<ProjectDTO> project;

        try {
            ProjectValidator.validateProjectId(projectId);

            project = projectDAO.findById(projectId);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }

        return project;
    }

    public List<ProjectDTO> findAllProjects()
            throws BusinessException {
        List<ProjectDTO> projects =
            projectDAO.findAll();

        return projects;
    }

    public List<ProjectDTO> findActiveProjects()
            throws BusinessException {
        List<ProjectDTO> projects =
            projectDAO.findByStatus(true);

        return projects;
    }

    public List<ProjectDTO> findInactiveProjects()
            throws BusinessException {
        List<ProjectDTO> projects =
            projectDAO.findByStatus(false);

        return projects;
    }

    public int countProjects()
            throws BusinessException {
        int totalProjects =
            projectDAO.countAll();

        return totalProjects;
    }
}