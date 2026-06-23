package mx.uv.internshipprogramsystem.logic.managers;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;
import mx.uv.internshipprogramsystem.logic.security.Permission;

public class AccessControlManager {

    public void validatePermission(
            UserDTO user,
            Permission permission
    ) throws BusinessException, DataAccessException {
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
            mx.uv.internshipprogramsystem.logic.dto.UserRole role = user.getRole();
            if (role == mx.uv.internshipprogramsystem.logic.dto.UserRole.ADMINISTRATOR) {
                hasPermission = hasAdministratorPermission(permission);
            } else if (role == mx.uv.internshipprogramsystem.logic.dto.UserRole.PROFESSOR) {
                hasPermission = hasProfessorPermission(user, permission);
            } else if (role == mx.uv.internshipprogramsystem.logic.dto.UserRole.STUDENT) {
                hasPermission = hasStudentPermission(permission);
            } else {
                hasPermission = false;
            }
        }

        return hasPermission;
    }

    private boolean hasAdministratorPermission(
            Permission permission
    ) {
        return permission == Permission.REGISTER_PROFESSOR
            || permission == Permission.CONSULT_PROFESSOR
            || permission == Permission.UPDATE_PROFESSOR
            || permission == Permission.CHANGE_PROFESSOR_STATUS;
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
        return permission == Permission.REGISTER_INTERN
            || permission == Permission.CONSULT_INTERN
            || permission == Permission.UPDATE_INTERN
            || permission == Permission.CHANGE_INTERN_STATUS
            || permission == Permission.REGISTER_ORGANIZATION
            || permission == Permission.CONSULT_ORGANIZATION
            || permission == Permission.REGISTER_PROJECT_RESPONSIBLE
            || permission == Permission.CONSULT_PROJECT_RESPONSIBLE
            || permission == Permission.REGISTER_PROJECT
            || permission == Permission.CONSULT_PROJECT
            || permission == Permission.UPDATE_PROJECT
            || permission == Permission.ADD_PROJECT_ACTIVITIES
            || permission == Permission.ASSIGN_PROJECT
            || permission == Permission.DELETE_PROJECT
            || permission == Permission.REGISTER_EDUCATIONAL_EXPERIENCE
            || permission == Permission.ASSIGN_EDUCATIONAL_EXPERIENCE
            || permission == Permission.VALIDATE_INITIAL_FORMATS
            || permission == Permission.UPLOAD_ORGANIZATION_EVALUATION
            || permission == Permission.EVALUATE_REPORT
            || permission == Permission.VALIDATE_PROJECT
            || permission == Permission.CONSULT_REPORT;
    }

    private boolean hasRegularProfessorPermission(
            Permission permission
    ) {
        return permission == Permission.CONSULT_PROJECT
            || permission == Permission.VALIDATE_INITIAL_FORMATS
            || permission == Permission.UPLOAD_ORGANIZATION_EVALUATION
            || permission == Permission.EVALUATE_REPORT
            || permission == Permission.VALIDATE_PROJECT
            || permission == Permission.CONSULT_REPORT;
    }

    private boolean hasStudentPermission(
            Permission permission
    ) {
        return permission == Permission.CONSULT_PROJECT
            || permission == Permission.REQUEST_PROJECT
            || permission == Permission.UPLOAD_INITIAL_FORMATS
            || permission == Permission.UPLOAD_SELF_EVALUATION
            || permission == Permission.GENERATE_SELF_EVALUATION
            || permission == Permission.PRINT_SELF_EVALUATION
            || permission == Permission.CONSULT_SELF_EVALUATION
            || permission == Permission.UPLOAD_REPORT
            || permission == Permission.GENERATE_REPORT
            || permission == Permission.PRINT_REPORT
            || permission == Permission.CONSULT_REPORT
            || permission == Permission.GENERATE_FINAL_REPORT;
    }
}