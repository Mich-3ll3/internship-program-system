package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProjectApplicationDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectApplicationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;

public class ProjectApplicationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectApplicationManager.class);
    private final ProjectApplicationDAO applicationDAO;

    public ProjectApplicationManager() {
        this.applicationDAO = new ProjectApplicationDAO();
    }

    public boolean registerApplication(int internId, int projectId, int priority) throws BusinessException {
        InputValidator.validatePositive(internId, "El identificador del estudiante debe ser positivo.");
        InputValidator.validatePositive(projectId, "El identificador del proyecto debe ser positivo.");

        final int MIN_PRIORITY = 1;
        final int MAX_PRIORITY = 3;
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
            throw new BusinessException("La prioridad seleccionada no es válida (Debe ser entre 1 y 3).");
        }

        boolean isRegistered = false;

        try {
            ProjectApplicationDTO application = new ProjectApplicationDTO();
            application.setInternId(internId);
            application.setProjectId(projectId);
            application.setPriority(priority);

            LOGGER.info("Iniciando registro de postulación para estudiante ID {} en proyecto ID {} con prioridad {}", 
                    internId, projectId, priority);

            isRegistered = applicationDAO.createApplication(application);
            
            if (isRegistered) {
                LOGGER.info("Postulación registrada exitosamente en la base de datos.");
            }

        } catch (BusinessException businessException) {
            LOGGER.error("Regla de negocio no superada al registrar postulación: {}", businessException.getMessage());
            throw businessException; 
        }

        return isRegistered;
    }

    public List<ProjectApplicationDTO> getActiveApplications(int internId) throws BusinessException {
        InputValidator.validatePositive(internId, "El identificador del estudiante debe ser positivo.");

        try {
            LOGGER.info("Consultando postulaciones activas para el estudiante ID: {}", internId);
            return applicationDAO.getApplicationsByStudent(internId);
        } catch (BusinessException businessException) {
            LOGGER.error("Error al consultar postulaciones: {}", businessException.getMessage());
            throw businessException;
        }
    }

    public boolean cancelApplication(int internId, int projectId) throws BusinessException {
        InputValidator.validatePositive(internId, "El identificador del estudiante debe ser positivo.");
        InputValidator.validatePositive(projectId, "El identificador del proyecto debe ser positivo.");

        try {
            LOGGER.info("Iniciando cancelación de postulación para estudiante ID {} en proyecto ID {}", internId, projectId);
            boolean isDeleted = applicationDAO.deleteApplication(internId, projectId);
            
            if (isDeleted) {
                LOGGER.info("Postulación cancelada exitosamente en la base de datos.");
            } else {
                LOGGER.warn("No se encontró la postulación para cancelar (estudiante: {}, proyecto: {})", internId, projectId);
            }
            return isDeleted;

        } catch (BusinessException businessException) {
            LOGGER.error("Error al cancelar postulación: {}", businessException.getMessage());
            throw businessException;
        }
    }
}