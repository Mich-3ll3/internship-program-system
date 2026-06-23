package mx.uv.internshipprogramsystem.gui.handlers;

import java.util.function.Predicate;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;

public class OrganizationPredicate implements Predicate<LinkedOrganizationDTO> {
    private final String query;

    public OrganizationPredicate(String query) {
        this.query = query;
    }

    @Override
    public boolean test(LinkedOrganizationDTO organization) {
        if (query.isBlank()) {
            return true;
        }
        String name = organization.getName();
        String email = organization.getEmail();
        String country = organization.getCountry();
        String state = organization.getState();
        String city = organization.getCity();
        String sector = organization.getSector();

        return contains(name, query)
            || contains(email, query)
            || contains(country, query)
            || contains(state, query)
            || contains(city, query)
            || contains(sector, query);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }
}
