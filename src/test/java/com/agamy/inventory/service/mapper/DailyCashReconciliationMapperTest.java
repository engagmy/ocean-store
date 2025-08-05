package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.DailyCashReconciliationAsserts.*;
import static com.agamy.inventory.domain.DailyCashReconciliationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DailyCashReconciliationMapperTest {

    private DailyCashReconciliationMapper dailyCashReconciliationMapper;

    @BeforeEach
    void setUp() {
        dailyCashReconciliationMapper = new DailyCashReconciliationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDailyCashReconciliationSample1();
        var actual = dailyCashReconciliationMapper.toEntity(dailyCashReconciliationMapper.toDto(expected));
        assertDailyCashReconciliationAllPropertiesEquals(expected, actual);
    }
}
