package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.ProductTestSamples.*;
import static com.agamy.inventory.domain.PurchaseOperationTestSamples.*;
import static com.agamy.inventory.domain.PurchasePaymentTestSamples.*;
import static com.agamy.inventory.domain.PurchaseTestSamples.*;
import static com.agamy.inventory.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PurchaseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Purchase.class);
        Purchase purchase1 = getPurchaseSample1();
        Purchase purchase2 = new Purchase();
        assertThat(purchase1).isNotEqualTo(purchase2);

        purchase2.setId(purchase1.getId());
        assertThat(purchase1).isEqualTo(purchase2);

        purchase2 = getPurchaseSample2();
        assertThat(purchase1).isNotEqualTo(purchase2);
    }

    @Test
    void purchaseOperationTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        PurchaseOperation purchaseOperationBack = getPurchaseOperationRandomSampleGenerator();

        purchase.setPurchaseOperation(purchaseOperationBack);
        assertThat(purchase.getPurchaseOperation()).isEqualTo(purchaseOperationBack);

        purchase.purchaseOperation(null);
        assertThat(purchase.getPurchaseOperation()).isNull();
    }

    @Test
    void productTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        purchase.setProduct(productBack);
        assertThat(purchase.getProduct()).isEqualTo(productBack);

        purchase.product(null);
        assertThat(purchase.getProduct()).isNull();
    }

    @Test
    void supplierTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        purchase.setSupplier(supplierBack);
        assertThat(purchase.getSupplier()).isEqualTo(supplierBack);

        purchase.supplier(null);
        assertThat(purchase.getSupplier()).isNull();
    }

    @Test
    void purchasePaymentTest() {
        Purchase purchase = getPurchaseRandomSampleGenerator();
        PurchasePayment purchasePaymentBack = getPurchasePaymentRandomSampleGenerator();

        purchase.addPurchasePayment(purchasePaymentBack);
        assertThat(purchase.getPurchasePayments()).containsOnly(purchasePaymentBack);
        assertThat(purchasePaymentBack.getPurchase()).isEqualTo(purchase);

        purchase.removePurchasePayment(purchasePaymentBack);
        assertThat(purchase.getPurchasePayments()).doesNotContain(purchasePaymentBack);
        assertThat(purchasePaymentBack.getPurchase()).isNull();

        purchase.purchasePayments(new HashSet<>(Set.of(purchasePaymentBack)));
        assertThat(purchase.getPurchasePayments()).containsOnly(purchasePaymentBack);
        assertThat(purchasePaymentBack.getPurchase()).isEqualTo(purchase);

        purchase.setPurchasePayments(new HashSet<>());
        assertThat(purchase.getPurchasePayments()).doesNotContain(purchasePaymentBack);
        assertThat(purchasePaymentBack.getPurchase()).isNull();
    }
}
