package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.PurchasePaymentTestSamples.*;
import static com.agamy.inventory.domain.PurchaseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchasePaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchasePayment.class);
        PurchasePayment purchasePayment1 = getPurchasePaymentSample1();
        PurchasePayment purchasePayment2 = new PurchasePayment();
        assertThat(purchasePayment1).isNotEqualTo(purchasePayment2);

        purchasePayment2.setId(purchasePayment1.getId());
        assertThat(purchasePayment1).isEqualTo(purchasePayment2);

        purchasePayment2 = getPurchasePaymentSample2();
        assertThat(purchasePayment1).isNotEqualTo(purchasePayment2);
    }

    @Test
    void purchaseTest() {
        PurchasePayment purchasePayment = getPurchasePaymentRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        purchasePayment.setPurchase(purchaseBack);
        assertThat(purchasePayment.getPurchase()).isEqualTo(purchaseBack);

        purchasePayment.purchase(null);
        assertThat(purchasePayment.getPurchase()).isNull();
    }
}
