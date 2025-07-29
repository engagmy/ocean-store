package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.service.dto.BillDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Bill} and its DTO {@link BillDTO}.
 */
@Mapper(componentModel = "spring")
public interface BillMapper extends EntityMapper<BillDTO, Bill> {}
