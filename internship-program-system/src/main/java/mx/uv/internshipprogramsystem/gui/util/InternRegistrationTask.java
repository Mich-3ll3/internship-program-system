package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.dto.UserDTO;
import mx.uv.internshipprogramsystem.logic.managers.InternRegistrationManager;

public class InternRegistrationTask extends Task<Boolean> {

    private final UserDTO user;
    private final InternDTO intern;

    public InternRegistrationTask(UserDTO user, InternDTO intern) {
        this.user = user;
        this.intern = intern;
    }

    @Override
    protected Boolean call() throws Exception {
        InternRegistrationManager manager = new InternRegistrationManager();
        return manager.registerIntern(user, intern);
    }
}
