package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.DailyCashDetail;
import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.service.dto.DailyCashDetailDTO;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DailyCashDetail} and its DTO {@link DailyCashDetailDTO}.
 */
@Mapper(componentModel = "spring")
public interface DailyCashDetailMapper extends EntityMapper<DailyCashDetailDTO, DailyCashDetail> {
    @Mapping(target = "dailyCashReconciliation", source = "dailyCashReconciliation", qualifiedByName = "dailyCashReconciliationId")
    DailyCashDetailDTO toDto(DailyCashDetail s);

    @Named("dailyCashReconciliationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DailyCashReconciliationDTO toDtoDailyCashReconciliationId(DailyCashReconciliation dailyCashReconciliation);
}
