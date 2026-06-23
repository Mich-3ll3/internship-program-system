package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.ProfessorDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IProfessorDAO {
    boolean create(ProfessorDTO professor, Connection connection) throws BusinessException, DataAccessException;
    boolean update(ProfessorDTO professor) throws BusinessException, DataAccessException;
    Optional<ProfessorDTO> findByStaffNumber(String staffNumber) throws BusinessException, DataAccessException;
    List<ProfessorDTO> findByName(String searchName) throws BusinessException, DataAccessException;
    List<ProfessorDTO> findAll() throws BusinessException, DataAccessException;
    List<ProfessorDTO> findAllName() throws BusinessException, DataAccessException;
    boolean existsCoordinator() throws BusinessException, DataAccessException;
    boolean hasActiveEducationalExperience(int professorId) throws BusinessException, DataAccessException;
    Optional<ProfessorDTO> findCoordinator() throws BusinessException, DataAccessException;
    int countAll() throws BusinessException, DataAccessException;
    int countActive() throws BusinessException, DataAccessException;
    int countInactive() throws BusinessException, DataAccessException;
    int countPending() throws BusinessException, DataAccessException;
}