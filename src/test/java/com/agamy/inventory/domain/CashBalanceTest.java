package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.CashBalanceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashBalanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashBalance.class);
        CashBalance cashBalance1 = getCashBalanceSample1();
        CashBalance cashBalance2 = new CashBalance();
        assertThat(cashBalance1).isNotEqualTo(cashBalance2);

        cashBalance2.setId(cashBalance1.getId());
        assertThat(cashBalance1).isEqualTo(cashBalance2);

        cashBalance2 = getCashBalanceSample2();
        assertThat(cashBalance1).isNotEqualTo(cashBalance2);
    }
}
