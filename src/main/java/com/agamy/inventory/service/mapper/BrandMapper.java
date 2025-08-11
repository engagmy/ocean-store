package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Brand;
import com.agamy.inventory.service.dto.BrandDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Brand} and its DTO {@link BrandDTO}.
 */
@Mapper(componentModel = "spring")
public interface BrandMapper extends EntityMapper<BrandDTO, Brand> {}
