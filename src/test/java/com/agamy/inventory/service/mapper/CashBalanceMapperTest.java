package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.CashBalanceAsserts.*;
import static com.agamy.inventory.domain.CashBalanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashBalanceMapperTest {

    private CashBalanceMapper cashBalanceMapper;

    @BeforeEach
    void setUp() {
        cashBalanceMapper = new CashBalanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCashBalanceSample1();
        var actual = cashBalanceMapper.toEntity(cashBalanceMapper.toDto(expected));
        assertCashBalanceAllPropertiesEquals(expected, actual);
    }
}
