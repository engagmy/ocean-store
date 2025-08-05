package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.PurchasePaymentAsserts.*;
import static com.agamy.inventory.domain.PurchasePaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchasePaymentMapperTest {

    private PurchasePaymentMapper purchasePaymentMapper;

    @BeforeEach
    void setUp() {
        purchasePaymentMapper = new PurchasePaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPurchasePaymentSample1();
        var actual = purchasePaymentMapper.toEntity(purchasePaymentMapper.toDto(expected));
        assertPurchasePaymentAllPropertiesEquals(expected, actual);
    }
}
