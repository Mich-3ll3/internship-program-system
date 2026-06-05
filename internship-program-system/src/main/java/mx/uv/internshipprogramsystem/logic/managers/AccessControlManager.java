package mx.uv.internshipprogramsystem.logic.managers;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class AccessControlManager {

    public void validatePermission(
            UserDTO user,
            Permission permission
    ) throws BusinessException {
        if (!hasPermission(user, permission)) {
            throw new BusinessException(
                "No tienes permisos para realizar esta acción."
            );
        }
    }

    public boolean hasPermission(
            UserDTO user,
            Permission permission
    ) {
        boolean hasPermission = false;

        if (user != null && permission != null && user.getRole() != null) {
            switch (user.getRole()) {
                case ADMINISTRATOR:
                    hasPermission = hasAdministratorPermission(permission);
                    break;
                case PROFESSOR:
                    hasPermission = hasProfessorPermission(user, permission);
                    break;
                case STUDENT:
                    hasPermission = hasStudentPermission(permission);
                    break;
                default:
                    hasPermission = false;
                    break;
            }
        }

        return hasPermission;
    }

    private boolean hasAdministratorPermission(
            Permission permission
    ) {
        boolean hasPermission;

        switch (permission) {
            case REGISTER_PROFESSOR:
            case CONSULT_PROFESSOR:
            case UPDATE_PROFESSOR:
            case CHANGE_PROFESSOR_STATUS:
                hasPermission = true;
                break;
            default:
                hasPermission = false;
                break;
        }

        return hasPermission;
    }

    private boolean hasProfessorPermission(
            UserDTO user,
            Permission permission
    ) {
        boolean hasPermission;

        if (isCoordinator(user)) {
            hasPermission = hasCoordinatorPermission(permission);
        } else {
            hasPermission = hasRegularProfessorPermission(permission);
        }

        return hasPermission;
    }

    private boolean isCoordinator(
            UserDTO user
    ) {
        boolean isCoordinator = false;

        if (user instanceof ProfessorDTO) {
            ProfessorDTO professor = (ProfessorDTO) user;
            isCoordinator = professor.getIsCoordinator();
        }

        return isCoordinator;
    }

    private boolean hasCoordinatorPermission(
            Permission permission
    ) {
        boolean hasPermission;

        switch (permission) {
            case REGISTER_INTERN:
            case CONSULT_INTERN:
            case UPDATE_INTERN:
            case CHANGE_INTERN_STATUS:
            case REGISTER_ORGANIZATION:
            case CONSULT_ORGANIZATION:
            case REGISTER_PROJECT_RESPONSIBLE:
            case CONSULT_PROJECT_RESPONSIBLE:
            case REGISTER_PROJECT:
            case CONSULT_PROJECT:
            case UPDATE_PROJECT:
            case ADD_PROJECT_ACTIVITIES:
            case ASSIGN_PROJECT:
            case DELETE_PROJECT:
            case REGISTER_EDUCATIONAL_EXPERIENCE:
            case ASSIGN_EDUCATIONAL_EXPERIENCE:

            case VALIDATE_INITIAL_FORMATS:
            case UPLOAD_ORGANIZATION_EVALUATION:
            case EVALUATE_REPORT:
            case VALIDATE_PROJECT:
                hasPermission = true;
                break;
            default:
                hasPermission = false;
                break;
        }

        return hasPermission;
    }

    private boolean hasRegularProfessorPermission(
            Permission permission
    ) {
        boolean hasPermission;

        switch (permission) {
            case CONSULT_PROJECT:
            case VALIDATE_INITIAL_FORMATS:
            case UPLOAD_ORGANIZATION_EVALUATION:
            case EVALUATE_REPORT:
            case VALIDATE_PROJECT:
                hasPermission = true;
                break;
            default:
                hasPermission = false;
                break;
        }

        return hasPermission;
    }

    private boolean hasStudentPermission(
            Permission permission
    ) {
        boolean hasPermission;

        switch (permission) {
            case CONSULT_PROJECT:
            case REQUEST_PROJECT:
            case UPLOAD_INITIAL_FORMATS:
            case UPLOAD_SELF_EVALUATION:
            case GENERATE_SELF_EVALUATION:
            case PRINT_SELF_EVALUATION:
            case CONSULT_SELF_EVALUATION:
            case UPLOAD_REPORT:
            case GENERATE_REPORT:
            case PRINT_REPORT:
            case CONSULT_REPORT:
            case GENERATE_FINAL_REPORT:
                hasPermission = true;
                break;
            default:
                hasPermission = false;
                break;
        }

        return hasPermission;
    }
}