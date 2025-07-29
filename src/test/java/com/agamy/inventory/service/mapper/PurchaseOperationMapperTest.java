package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.PurchaseOperationAsserts.*;
import static com.agamy.inventory.domain.PurchaseOperationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOperationMapperTest {

    private PurchaseOperationMapper purchaseOperationMapper;

    @BeforeEach
    void setUp() {
        purchaseOperationMapper = new PurchaseOperationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchaseOperationSample1();
        var actual = purchaseOperationMapper.toEntity(purchaseOperationMapper.toDto(expected));
        assertPurchaseOperationAllPropertiesEquals(expected, actual);
    }
}
