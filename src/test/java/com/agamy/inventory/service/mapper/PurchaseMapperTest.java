package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.PurchaseAsserts.*;
import static com.agamy.inventory.domain.PurchaseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseMapperTest {

    private PurchaseMapper purchaseMapper;

    @BeforeEach
    void setUp() {
        purchaseMapper = new PurchaseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseSample1();
        var actual = purchaseMapper.toEntity(purchaseMapper.toDto(expected));
        assertPurchaseAllPropertiesEquals(expected, actual);
    }
}
