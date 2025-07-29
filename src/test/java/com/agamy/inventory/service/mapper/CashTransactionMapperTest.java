package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.CashTransactionAsserts.*;
import static com.agamy.inventory.domain.CashTransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashTransactionMapperTest {

    private CashTransactionMapper cashTransactionMapper;

    @BeforeEach
    void setUp() {
        cashTransactionMapper = new CashTransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCashTransactionSample1();
        var actual = cashTransactionMapper.toEntity(cashTransactionMapper.toDto(expected));
        assertCashTransactionAllPropertiesEquals(expected, actual);
    }
}
