package mx.uv.internshipprogramsystem.logic.interfaces;

import java.util.List;
import mx.uv.internshipprogramsystem.logic.dto.LinkedOrganizationDTO;
import mx.uv.internshipprogramsystem.logic.exceptions.BusinessException;

public interface ILinkedOrganizationDAO {
    boolean createLikendOrganization(LinkedOrganizationDTO linkedOrganization) throws BusinessException;
    List<LinkedOrganizationDTO> findAll() throws BusinessException;
    boolean update(LinkedOrganizationDTO linkedOrganization) throws BusinessException;
}
