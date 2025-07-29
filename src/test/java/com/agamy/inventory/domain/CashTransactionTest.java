package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.CashTransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashTransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashTransaction.class);
        CashTransaction cashTransaction1 = getCashTransactionSample1();
        CashTransaction cashTransaction2 = new CashTransaction();
        assertThat(cashTransaction1).isNotEqualTo(cashTransaction2);

        cashTransaction2.setId(cashTransaction1.getId());
        assertThat(cashTransaction1).isEqualTo(cashTransaction2);

        cashTransaction2 = getCashTransactionSample2();
        assertThat(cashTransaction1).isNotEqualTo(cashTransaction2);
    }
}
