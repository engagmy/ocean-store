package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.InventoryTransaction;
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.service.dto.InventoryTransactionDTO;
import com.agamy.inventory.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryTransaction} and its DTO {@link InventoryTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper extends EntityMapper<InventoryTransactionDTO, InventoryTransaction> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    InventoryTransactionDTO toDto(InventoryTransaction s);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
