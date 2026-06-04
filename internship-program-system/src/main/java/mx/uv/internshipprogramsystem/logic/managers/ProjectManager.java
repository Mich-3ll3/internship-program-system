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

    public boolean registerProject(ProjectDTO project)
            throws BusinessException {
        try {
            ProjectValidator.validateForCreate(project);

            return projectDAO.create(project);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    public boolean updateProject(ProjectDTO project)
            throws BusinessException {
        try {
            ProjectValidator.validateForUpdate(project);

            return projectDAO.update(project);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    public boolean deactivateProject(int projectId)
            throws BusinessException {
        try {
            ProjectValidator.validateProjectId(projectId);

            return projectDAO.deactivate(projectId);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    public Optional<ProjectDTO> findProjectById(
            int projectId
    ) throws BusinessException {
        try {
            ProjectValidator.validateProjectId(projectId);

            return projectDAO.findById(projectId);
        } catch (ValidationException validationException) {
            throw new BusinessException(
                validationException.getMessage(),
                validationException
            );
        }
    }

    public List<ProjectDTO> findAllProjects()
            throws BusinessException {
        return projectDAO.findAll();
    }

    public List<ProjectDTO> findActiveProjects()
            throws BusinessException {
        return projectDAO.findByStatus(true);
    }

    public List<ProjectDTO> findInactiveProjects()
            throws BusinessException {
        return projectDAO.findByStatus(false);
    }

    public int countProjects()
            throws BusinessException {
        return projectDAO.countAll();
    }
}