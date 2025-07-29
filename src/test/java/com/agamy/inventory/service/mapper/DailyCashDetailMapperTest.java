package com.agamy.inventory.service.mapper;

import static com.agamy.inventory.domain.DailyCashDetailAsserts.*;
import static com.agamy.inventory.domain.DailyCashDetailTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DailyCashDetailMapperTest {

    private DailyCashDetailMapper dailyCashDetailMapper;

    @BeforeEach
    void setUp() {
        dailyCashDetailMapper = new DailyCashDetailMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDailyCashDetailSample1();
        var actual = dailyCashDetailMapper.toEntity(dailyCashDetailMapper.toDto(expected));
        assertDailyCashDetailAllPropertiesEquals(expected, actual);
    }
}
