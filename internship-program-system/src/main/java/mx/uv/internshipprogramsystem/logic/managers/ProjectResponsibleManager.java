package mx.uv.internshipprogramsystem.logic.managers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.uv.internshipprogramsystem.logic.dao.ProjectResponsibleDAO;
import mx.uv.internshipprogramsystem.logic.dto.ProjectResponsibleDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.validations.InputValidator;
import mx.uv.internshipprogramsystem.logic.validations.ProjectResponsibleValidator;

public class ProjectResponsibleManager {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
            ProjectResponsibleManager.class
        );

    private final ProjectResponsibleDAO projectResponsibleDAO;

    private final ProjectResponsibleValidator projectResponsibleValidator;

    public ProjectResponsibleManager() {
        projectResponsibleDAO =
            new ProjectResponsibleDAO();

        projectResponsibleValidator =
            new ProjectResponsibleValidator();
    }

    public boolean registerProjectResponsible(
            ProjectResponsibleDTO responsible
    ) throws BusinessException {
        projectResponsibleValidator.validateForRegistration(
            responsible
        );

        boolean wasRegistered =
            projectResponsibleDAO.insert(
                responsible
            );

        if (wasRegistered) {
            LOGGER.info(
                "Caso de uso registrar responsable completado correctamente."
            );
        }

        return wasRegistered;
    }

    public List<ProjectResponsibleDTO> getAllProjectResponsibles()
            throws BusinessException {
        List<ProjectResponsibleDTO> projectResponsibles =
            projectResponsibleDAO.findAll();

        LOGGER.info(
            "Caso de uso consultar responsables completado correctamente."
        );

        return List.copyOf(
            projectResponsibles
        );
    }

    public List<ProjectResponsibleDTO> searchProjectResponsibles(
            String searchText
    ) throws BusinessException {
        InputValidator.validateNotEmpty(
            searchText,
            "El texto de búsqueda no puede estar vacío."
        );

        List<ProjectResponsibleDTO> projectResponsibles =
            projectResponsibleDAO.findBySearchText(
                searchText.trim()
            );

        LOGGER.info(
            "Caso de uso buscar responsables completado correctamente."
        );

        return List.copyOf(
            projectResponsibles
        );
    }
}