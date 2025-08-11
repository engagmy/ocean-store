package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.SalePaymentTestSamples.*;
import static com.agamy.inventory.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalePaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalePayment.class);
        SalePayment salePayment1 = getSalePaymentSample1();
        SalePayment salePayment2 = new SalePayment();
        assertThat(salePayment1).isNotEqualTo(salePayment2);

        salePayment2.setId(salePayment1.getId());
        assertThat(salePayment1).isEqualTo(salePayment2);

        salePayment2 = getSalePaymentSample2();
        assertThat(salePayment1).isNotEqualTo(salePayment2);
    }

    @Test
    void saleTest() {
        SalePayment salePayment = getSalePaymentRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        salePayment.setSale(saleBack);
        assertThat(salePayment.getSale()).isEqualTo(saleBack);

        salePayment.sale(null);
        assertThat(salePayment.getSale()).isNull();
    }
}
