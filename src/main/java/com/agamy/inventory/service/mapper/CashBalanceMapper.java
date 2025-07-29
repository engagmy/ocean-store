package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.CashBalance;
import com.agamy.inventory.service.dto.CashBalanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CashBalance} and its DTO {@link CashBalanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface CashBalanceMapper extends EntityMapper<CashBalanceDTO, CashBalance> {}
