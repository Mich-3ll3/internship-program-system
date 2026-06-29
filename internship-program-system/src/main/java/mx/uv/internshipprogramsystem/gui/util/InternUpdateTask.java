package mx.uv.internshipprogramsystem.gui.util;

import javafx.concurrent.Task;
import mx.uv.internshipprogramsystem.logic.dao.InternDAO;
import mx.uv.internshipprogramsystem.logic.dao.UserDAO;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;

public class InternUpdateTask extends Task<Boolean> {

    private final InternDTO intern;

    public InternUpdateTask(InternDTO intern) {
        this.intern = intern;
    }

    @Override
    protected Boolean call() throws Exception {
        UserDAO userDAO = new UserDAO();
        boolean userUpdated = userDAO.update(intern);
        
        InternDAO internDAO = new InternDAO();
        boolean internUpdated = internDAO.update(intern);
        
        return userUpdated && internUpdated;
    }
}
