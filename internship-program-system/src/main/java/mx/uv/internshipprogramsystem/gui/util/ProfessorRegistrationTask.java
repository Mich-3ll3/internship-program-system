package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.ProfessorRegistrationManager;

public class ProfessorRegistrationTask extends Task<Boolean> {

    private final UserDTO user;
    private final ProfessorDTO professor;

    public ProfessorRegistrationTask(
        UserDTO user,
        ProfessorDTO professor
    ) {
        this.user = user;
        this.professor = professor;
    }

    @Override
    protected Boolean call() throws Exception {
        ProfessorRegistrationManager manager =
            new ProfessorRegistrationManager();
        return manager.registerProfessor(user, professor);
    }
}
