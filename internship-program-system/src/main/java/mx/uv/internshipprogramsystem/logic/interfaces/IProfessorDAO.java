package mx.uv.internshipprogramsystem.logic.interfaces;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProfessorDAO {
    boolean create(ProfessorDTO professor, Connection connection) throws BusinessException;
    boolean update(ProfessorDTO professor) throws BusinessException;
    Optional<ProfessorDTO> findByStaffNumber(String staffNumber) throws BusinessException;
    List<ProfessorDTO> findByName(String searchName) throws BusinessException;
    List<ProfessorDTO> findAll() throws BusinessException;
    List<ProfessorDTO> findAllName() throws BusinessException;
    boolean existsCoordinator() throws BusinessException;
    Optional<ProfessorDTO> findCoordinator() throws BusinessException;
    int countAll() throws BusinessException;
}
