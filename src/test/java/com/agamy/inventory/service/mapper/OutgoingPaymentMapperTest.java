package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.OutgoingPaymentAsserts.*;
import static com.agamy.inventory.domain.OutgoingPaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutgoingPaymentMapperTest {

    private OutgoingPaymentMapper outgoingPaymentMapper;

    @BeforeEach
    void setUp() {
        outgoingPaymentMapper = new OutgoingPaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOutgoingPaymentSample1();
        var actual = outgoingPaymentMapper.toEntity(outgoingPaymentMapper.toDto(expected));
        assertOutgoingPaymentAllPropertiesEquals(expected, actual);
    }
}
