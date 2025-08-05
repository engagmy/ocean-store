package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.Sale;
import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.service.dto.ProductDTO;
import com.agamy.inventory.service.dto.SaleDTO;
import com.agamy.inventory.service.dto.SaleOperationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sale} and its DTO {@link SaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleMapper extends EntityMapper<SaleDTO, Sale> {
    @Mapping(target = "saleOperation", source = "saleOperation", qualifiedByName = "saleOperationId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    SaleDTO toDto(Sale s);

    @Named("saleOperationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleOperationDTO toDtoSaleOperationId(SaleOperation saleOperation);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);
}
