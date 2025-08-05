package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.BillTestSamples.*;
import static com.agamy.inventory.domain.PurchaseOperationTestSamples.*;
import static com.agamy.inventory.domain.PurchaseTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PurchaseOperationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOperation.class);
        PurchaseOperation purchaseOperation1 = getPurchaseOperationSample1();
        PurchaseOperation purchaseOperation2 = new PurchaseOperation();
        assertThat(purchaseOperation1).isNotEqualTo(purchaseOperation2);

        purchaseOperation2.setId(purchaseOperation1.getId());
        assertThat(purchaseOperation1).isEqualTo(purchaseOperation2);

        purchaseOperation2 = getPurchaseOperationSample2();
        assertThat(purchaseOperation1).isNotEqualTo(purchaseOperation2);
    }

    @Test
    void billTest() {
        PurchaseOperation purchaseOperation = getPurchaseOperationRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        purchaseOperation.setBill(billBack);
        assertThat(purchaseOperation.getBill()).isEqualTo(billBack);

        purchaseOperation.bill(null);
        assertThat(purchaseOperation.getBill()).isNull();
    }

    @Test
    void purchaseTest() {
        PurchaseOperation purchaseOperation = getPurchaseOperationRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        purchaseOperation.addPurchase(purchaseBack);
        assertThat(purchaseOperation.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getPurchaseOperation()).isEqualTo(purchaseOperation);

        purchaseOperation.removePurchase(purchaseBack);
        assertThat(purchaseOperation.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getPurchaseOperation()).isNull();

        purchaseOperation.purchases(new HashSet<>(Set.of(purchaseBack)));
        assertThat(purchaseOperation.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getPurchaseOperation()).isEqualTo(purchaseOperation);

        purchaseOperation.setPurchases(new HashSet<>());
        assertThat(purchaseOperation.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getPurchaseOperation()).isNull();
    }
}
