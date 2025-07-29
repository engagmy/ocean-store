package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Brand;
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.domain.ProductCategory;
import com.agamy.inventory.service.dto.BrandDTO;
import com.agamy.inventory.service.dto.ProductCategoryDTO;
import com.agamy.inventory.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "brand", source = "brand", qualifiedByName = "brandId")
    @Mapping(target = "productCategory", source = "productCategory", qualifiedByName = "productCategoryId")
    ProductDTO toDto(Product s);

    @Named("brandId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BrandDTO toDtoBrandId(Brand brand);

    @Named("productCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductCategoryDTO toDtoProductCategoryId(ProductCategory productCategory);
}
