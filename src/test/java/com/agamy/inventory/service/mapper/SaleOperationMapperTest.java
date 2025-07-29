package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.SaleOperationAsserts.*;
import static com.agamy.inventory.domain.SaleOperationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaleOperationMapperTest {

    private SaleOperationMapper saleOperationMapper;

    @BeforeEach
    void setUp() {
        saleOperationMapper = new SaleOperationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSaleOperationSample1();
        var actual = saleOperationMapper.toEntity(saleOperationMapper.toDto(expected));
        assertSaleOperationAllPropertiesEquals(expected, actual);
    }
}
