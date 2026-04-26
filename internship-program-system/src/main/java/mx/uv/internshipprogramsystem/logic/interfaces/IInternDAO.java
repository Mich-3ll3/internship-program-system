package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.InternDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface IInternDAO {
    boolean create(InternDTO intern) throws BusinessException;
    InternDTO findByMatricula(String enrollmentNumber) throws BusinessException;
    List<InternDTO> findAll() throws BusinessException;
    boolean update(InternDTO intern) throws BusinessException;
}
