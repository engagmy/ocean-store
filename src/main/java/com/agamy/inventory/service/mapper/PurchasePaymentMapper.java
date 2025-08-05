package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.domain.PurchasePayment;
import com.agamy.inventory.service.dto.PurchaseDTO;
import com.agamy.inventory.service.dto.PurchasePaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchasePayment} and its DTO {@link PurchasePaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchasePaymentMapper extends EntityMapper<PurchasePaymentDTO, PurchasePayment> {
    @Mapping(target = "purchase", source = "purchase", qualifiedByName = "purchaseId")
    PurchasePaymentDTO toDto(PurchasePayment s);

    @Named("purchaseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseDTO toDtoPurchaseId(Purchase purchase);
}
