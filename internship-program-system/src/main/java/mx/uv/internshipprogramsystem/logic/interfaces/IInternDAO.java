package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IInternDAO {
    boolean create(InternDTO intern, Connection connection) throws BusinessException, DataAccessException;
    Optional<InternDTO> findByEnrollmentNumber(String enrollmentNumber) throws BusinessException, DataAccessException;
    List<InternDTO> findAll() throws BusinessException, DataAccessException;
    boolean update(InternDTO intern) throws BusinessException, DataAccessException;
    int countAll() throws BusinessException, DataAccessException;
}