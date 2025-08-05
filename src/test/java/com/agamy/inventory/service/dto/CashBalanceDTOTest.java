package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashBalanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashBalanceDTO.class);
        CashBalanceDTO cashBalanceDTO1 = new CashBalanceDTO();
        cashBalanceDTO1.setId(1L);
        CashBalanceDTO cashBalanceDTO2 = new CashBalanceDTO();
        assertThat(cashBalanceDTO1).isNotEqualTo(cashBalanceDTO2);
        cashBalanceDTO2.setId(cashBalanceDTO1.getId());
        assertThat(cashBalanceDTO1).isEqualTo(cashBalanceDTO2);
        cashBalanceDTO2.setId(2L);
        assertThat(cashBalanceDTO1).isNotEqualTo(cashBalanceDTO2);
        cashBalanceDTO1.setId(null);
        assertThat(cashBalanceDTO1).isNotEqualTo(cashBalanceDTO2);
    }
}
