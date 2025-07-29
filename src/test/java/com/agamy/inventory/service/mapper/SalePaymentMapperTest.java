package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.SalePaymentAsserts.*;
import static com.agamy.inventory.domain.SalePaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalePaymentMapperTest {

    private SalePaymentMapper salePaymentMapper;

    @BeforeEach
    void setUp() {
        salePaymentMapper = new SalePaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSalePaymentSample1();
        var actual = salePaymentMapper.toEntity(salePaymentMapper.toDto(expected));
        assertSalePaymentAllPropertiesEquals(expected, actual);
    }
}
