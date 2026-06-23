package mx.uv.internshipprogramsystem.logic.interfaces;
import mx.uv.internshipprogramsystem.logic.exceptions.DataAccessException;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface ILinkedOrganizationDAO {
    boolean createLinkedOrganization(LinkedOrganizationDTO linkedOrganization)
            throws BusinessException, DataAccessException;

    List<LinkedOrganizationDTO> findAll() throws BusinessException, DataAccessException;

    boolean update(LinkedOrganizationDTO linkedOrganization) throws BusinessException, DataAccessException;
}