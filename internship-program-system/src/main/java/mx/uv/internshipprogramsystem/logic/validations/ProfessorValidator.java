package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public class ProfessorValidator {

    private static final String STAFF_NUMBER_REGEX = "^\\d{6}$";

    public void validateStaffNumber(int staffNumber) throws BusinessException {
        String staffNumberStr = String.valueOf(staffNumber);

        if (staffNumberStr == null || staffNumberStr.trim().isEmpty()) {
            throw new BusinessException("El número de personal no puede estar vacío.");
        }

        if (!staffNumberStr.matches(STAFF_NUMBER_REGEX)) {
            throw new BusinessException("El número de personal debe tener exactamente 6 dígitos numéricos.");
        }
    }
}

