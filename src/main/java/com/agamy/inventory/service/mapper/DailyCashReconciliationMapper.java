package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DailyCashReconciliation} and its DTO {@link DailyCashReconciliationDTO}.
 */
@Mapper(componentModel = "spring")
public interface DailyCashReconciliationMapper extends EntityMapper<DailyCashReconciliationDTO, DailyCashReconciliation> {}
