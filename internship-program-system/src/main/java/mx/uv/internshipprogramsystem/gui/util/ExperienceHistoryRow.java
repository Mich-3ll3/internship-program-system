package mx.uv.internshipprogramsystem.gui.util;

public final class ExperienceHistoryRow {
    private final String nrc;
    private final String period;
    private final String section;
    private final String status;

    public ExperienceHistoryRow(
            String nrc,
            String period,
            String section,
            String status
    ) {
        this.nrc = nrc;
        this.period = period;
        this.section = section != null ? section.replace("Seccion ", "") : "";
        this.status = status != null ? status.replace("_", " ") : "";
    }

    public String getNrc() {
        return nrc;
    }

    public String getPeriod() {
        return period;
    }

    public String getSection() {
        return section;
    }

    public String getStatus() {
        return status;
    }
}
