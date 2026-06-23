package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dao.ProfessorDAO;

public class CoordinatorCheckTask extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {
        ProfessorDAO professorDAO = new ProfessorDAO();
        return professorDAO.existsCoordinator();
    }
}
