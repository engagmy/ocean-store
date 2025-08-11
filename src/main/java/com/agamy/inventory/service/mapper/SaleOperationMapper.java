package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.domain.Customer;
import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.service.dto.BillDTO;
import com.agamy.inventory.service.dto.CustomerDTO;
import com.agamy.inventory.service.dto.SaleOperationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleOperation} and its DTO {@link SaleOperationDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleOperationMapper extends EntityMapper<SaleOperationDTO, SaleOperation> {
    @Mapping(target = "bill", source = "bill", qualifiedByName = "billId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    SaleOperationDTO toDto(SaleOperation s);

    @Named("billId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BillDTO toDtoBillId(Bill bill);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
