package mx.uv.internshipprogramsystem.logic.validations;

import java.util.regex.Pattern;

import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.ValidationException;

public class LinkedOrganizationValidator {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^\\d{10}$";

    public void validateFullOrganization(LinkedOrganizationDTO organization)
            throws ValidationException {
        
        if (organization == null) {
            throw new ValidationException("La organización no puede ser nula.");
        }

        java.util.List<String> errors = new java.util.ArrayList<>();

        if (isNullOrEmpty(organization.getName())) {
            errors.add("El campo nombre es obligatorio.");
        }
        if (isNullOrEmpty(organization.getAddress())) {
            errors.add("El campo dirección es obligatorio.");
        }
        if (isNullOrEmpty(organization.getCountry())) {
            errors.add("El campo país es obligatorio.");
        }
        if (isNullOrEmpty(organization.getState())) {
            errors.add("El campo estado es obligatorio.");
        }
        if (isNullOrEmpty(organization.getCity())) {
            errors.add("El campo ciudad es obligatorio.");
        }
        if (isNullOrEmpty(organization.getSector())) {
            errors.add("El campo sector es obligatorio.");
        }

        try {
            validateEmailFormat(organization.getEmail());
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validatePhoneNumber(organization.getPhoneNumber());
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        try {
            validateUserCounts(
                organization.getDirectUserCount(),
                organization.getIndirectUserCount()
            );
        } catch (ValidationException e) {
            errors.addAll(e.getErrors());
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateEmailFormat(String email) throws ValidationException {
        
        if (email == null || !Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            throw new ValidationException("El formato del correo electrónico es inválido.");
        }
    }

    private void validatePhoneNumber(String phone) throws ValidationException {
        
        if (phone == null || !Pattern.compile(PHONE_REGEX).matcher(phone).matches()) {
            throw new ValidationException(
                "El teléfono debe contener exactamente 10 dígitos numéricos."
            );
        }
    }

    private void validateUserCounts(Integer direct, Integer indirect)
            throws ValidationException {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (direct == null || direct < 0) {
            errors.add("El número de usuarios directos no puede ser negativo.");
        }
        if (indirect == null || indirect < 0) {
            errors.add("El número de usuarios indirectos no puede ser negativo.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean isNullOrEmpty(String value) {
        
        return value == null || value.trim().isEmpty();
    }
}
