package mx.uv.internshipprogramsystem.logic.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx.uv.internshipprogramsystem.logic.dto.ReportDeadlineDTO;

public class ReportDeadlineDAO {
    private static final List<ReportDeadlineDTO> DEADLINES = new ArrayList<>();

    public void setDeadline(ReportDeadlineDTO deadline) {
        DEADLINES.removeIf(d -> d.getProfessorId() == deadline.getProfessorId() 
                             && d.getReportNumber() == deadline.getReportNumber());
        DEADLINES.add(deadline);
    }

    public Optional<ReportDeadlineDTO> getDeadline(int professorId, int reportNumber) {
        return DEADLINES.stream()
                .filter(d -> d.getProfessorId() == professorId && d.getReportNumber() == reportNumber)
                .findFirst();
    }
}
