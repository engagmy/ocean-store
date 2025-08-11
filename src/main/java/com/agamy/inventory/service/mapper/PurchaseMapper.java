package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.domain.Supplier;
import com.agamy.inventory.service.dto.ProductDTO;
import com.agamy.inventory.service.dto.PurchaseDTO;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
import com.agamy.inventory.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Purchase} and its DTO {@link PurchaseDTO}.
 */
@Mapper(componentModel = "spring")
public interface PurchaseMapper extends EntityMapper<PurchaseDTO, Purchase> {
    @Mapping(target = "purchaseOperation", source = "purchaseOperation", qualifiedByName = "purchaseOperationId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productId")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierId")
    PurchaseDTO toDto(Purchase s);

    @Named("purchaseOperationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseOperationDTO toDtoPurchaseOperationId(PurchaseOperation purchaseOperation);

    @Named("productId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductDTO toDtoProductId(Product product);

    @Named("supplierId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SupplierDTO toDtoSupplierId(Supplier supplier);
}
