package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.SalaryPaymentAsserts.*;
import static com.agamy.inventory.domain.SalaryPaymentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalaryPaymentMapperTest {

    private SalaryPaymentMapper salaryPaymentMapper;

    @BeforeEach
    void setUp() {
        salaryPaymentMapper = new SalaryPaymentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSalaryPaymentSample1();
        var actual = salaryPaymentMapper.toEntity(salaryPaymentMapper.toDto(expected));
        assertSalaryPaymentAllPropertiesEquals(expected, actual);
    }
}
