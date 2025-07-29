package com.agamy.inventory.service.mapper;

import com.agamy.inventory.domain.OutgoingPayment;
import com.agamy.inventory.service.dto.OutgoingPaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OutgoingPayment} and its DTO {@link OutgoingPaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface OutgoingPaymentMapper extends EntityMapper<OutgoingPaymentDTO, OutgoingPayment> {}
