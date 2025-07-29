package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.InventoryTransactionAsserts.*;
import static com.agamy.inventory.domain.InventoryTransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryTransactionMapperTest {

    private InventoryTransactionMapper inventoryTransactionMapper;

    @BeforeEach
    void setUp() {
        inventoryTransactionMapper = new InventoryTransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInventoryTransactionSample1();
        var actual = inventoryTransactionMapper.toEntity(inventoryTransactionMapper.toDto(expected));
        assertInventoryTransactionAllPropertiesEquals(expected, actual);
    }
}
