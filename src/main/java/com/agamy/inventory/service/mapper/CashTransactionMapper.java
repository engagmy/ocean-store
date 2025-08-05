package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.CashTransaction;
import com.agamy.inventory.service.dto.CashTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CashTransaction} and its DTO {@link CashTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CashTransactionMapper extends EntityMapper<CashTransactionDTO, CashTransaction> {}
