package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Sale;
import com.agamy.inventory.domain.SalePayment;
import com.agamy.inventory.service.dto.SaleDTO;
import com.agamy.inventory.service.dto.SalePaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SalePayment} and its DTO {@link SalePaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface SalePaymentMapper extends EntityMapper<SalePaymentDTO, SalePayment> {
    @Mapping(target = "sale", source = "sale", qualifiedByName = "saleId")
    SalePaymentDTO toDto(SalePayment s);

    @Named("saleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleDTO toDtoSaleId(Sale sale);
}
