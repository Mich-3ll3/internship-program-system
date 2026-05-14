package mx.uv.internshipprogramsystem.logic.validations;

import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;
import java.util.regex.Pattern;

public class LinkedOrganizationValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^\\d{10}$";

    public void validateFullOrganization(LinkedOrganizationDTO organization) throws ValidationException {
        if (organization == null) {
            throw new ValidationException("La organización no puede ser nula.");
        }

        validateRequiredFields(organization);
        validateEmailFormat(organization.getEmail());
        validatePhoneNumber(organization.getPhoneNumber());
        validateUserCounts(organization.getDirectUserCount(), organization.getIndirectUserCount());
    }

    private void validateRequiredFields(LinkedOrganizationDTO org) throws ValidationException {
        if (isNullOrEmpty(org.getName()) || isNullOrEmpty(org.getAddress()) || 
            isNullOrEmpty(org.getCity()) || isNullOrEmpty(org.getState()) || 
            isNullOrEmpty(org.getSector())) {
            throw new ValidationException("Todos los campos marcados como obligatorios deben ser completados.");
        }
    }

    private void validateEmailFormat(String email) throws ValidationException {
        if (email == null || !Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            throw new ValidationException("El formato del correo electrónico es inválido.");
        }
    }

    private void validatePhoneNumber(String phone) throws ValidationException {
        if (phone == null || !Pattern.compile(PHONE_REGEX).matcher(phone).matches()) {
            throw new ValidationException("El teléfono debe contener exactamente 10 dígitos numéricos.");
        }
    }

    private void validateUserCounts(Integer direct, Integer indirect) throws ValidationException {
        if (direct == null || direct < 0 || indirect == null || indirect < 0) {
            throw new ValidationException("El número de usuarios no puede ser negativo.");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}