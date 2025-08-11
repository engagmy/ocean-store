package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Supplier;
import com.agamy.inventory.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Supplier} and its DTO {@link SupplierDTO}.
 */
@Mapper(componentModel = "spring")
public interface SupplierMapper extends EntityMapper<SupplierDTO, Supplier> {}
