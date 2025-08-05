package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.service.dto.BillDTO;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseOperation} and its DTO {@link PurchaseOperationDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseOperationMapper extends EntityMapper<PurchaseOperationDTO, PurchaseOperation> {
    @Mapping(target = "bill", source = "bill", qualifiedByName = "billId")
    PurchaseOperationDTO toDto(PurchaseOperation s);

    @Named("billId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BillDTO toDtoBillId(Bill bill);
}
