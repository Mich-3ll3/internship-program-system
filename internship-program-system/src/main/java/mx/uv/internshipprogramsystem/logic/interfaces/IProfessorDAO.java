package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProfessorDAO {
    boolean create(ProfessorDTO professor) throws BusinessException;
    boolean update(ProfessorDTO professor) throws BusinessException;
    ProfessorDTO findByStaffNumber(String staffNumber) throws BusinessException;
    List<ProfessorDTO> findAll() throws BusinessException;
    ProfessorDTO findCoordinator() throws BusinessException;
}
