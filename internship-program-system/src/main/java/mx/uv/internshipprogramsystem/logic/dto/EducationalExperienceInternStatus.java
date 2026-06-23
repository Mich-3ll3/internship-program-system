package mx.uv.internshipprogramsystem.logic.dto;

public enum EducationalExperienceInternStatus {
    ACTIVA,
    APROBADA,
    REPROBADA,
    BAJA_NO_CONTABILIZADA,
    BAJA_EXTEMPORANEA;

    public String getDisplayName() {
        String displayName;

        switch (this) {
            case ACTIVA:
                displayName = "Activa";
                break;
            case APROBADA:
                displayName = "Aprobada";
                break;
            case REPROBADA:
                displayName = "Reprobada";
                break;
            case BAJA_NO_CONTABILIZADA:
                displayName = "Baja no contabilizada";
                break;
            case BAJA_EXTEMPORANEA:
                displayName = "Baja extemporanea";
                break;
            default:
                displayName = name();
                break;
        }

        return displayName;
    }
}
