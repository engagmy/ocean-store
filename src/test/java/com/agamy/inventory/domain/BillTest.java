package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.BillTestSamples.*;
import static com.agamy.inventory.domain.PurchaseOperationTestSamples.*;
import static com.agamy.inventory.domain.SaleOperationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bill.class);
        Bill bill1 = getBillSample1();
        Bill bill2 = new Bill();
        assertThat(bill1).isNotEqualTo(bill2);

        bill2.setId(bill1.getId());
        assertThat(bill1).isEqualTo(bill2);

        bill2 = getBillSample2();
        assertThat(bill1).isNotEqualTo(bill2);
    }

    @Test
    void saleOperationTest() {
        Bill bill = getBillRandomSampleGenerator();
        SaleOperation saleOperationBack = getSaleOperationRandomSampleGenerator();

        bill.addSaleOperation(saleOperationBack);
        assertThat(bill.getSaleOperations()).containsOnly(saleOperationBack);
        assertThat(saleOperationBack.getBill()).isEqualTo(bill);

        bill.removeSaleOperation(saleOperationBack);
        assertThat(bill.getSaleOperations()).doesNotContain(saleOperationBack);
        assertThat(saleOperationBack.getBill()).isNull();

        bill.saleOperations(new HashSet<>(Set.of(saleOperationBack)));
        assertThat(bill.getSaleOperations()).containsOnly(saleOperationBack);
        assertThat(saleOperationBack.getBill()).isEqualTo(bill);

        bill.setSaleOperations(new HashSet<>());
        assertThat(bill.getSaleOperations()).doesNotContain(saleOperationBack);
        assertThat(saleOperationBack.getBill()).isNull();
    }

    @Test
    void purchaseOperationTest() {
        Bill bill = getBillRandomSampleGenerator();
        PurchaseOperation purchaseOperationBack = getPurchaseOperationRandomSampleGenerator();

        bill.addPurchaseOperation(purchaseOperationBack);
        assertThat(bill.getPurchaseOperations()).containsOnly(purchaseOperationBack);
        assertThat(purchaseOperationBack.getBill()).isEqualTo(bill);

        bill.removePurchaseOperation(purchaseOperationBack);
        assertThat(bill.getPurchaseOperations()).doesNotContain(purchaseOperationBack);
        assertThat(purchaseOperationBack.getBill()).isNull();

        bill.purchaseOperations(new HashSet<>(Set.of(purchaseOperationBack)));
        assertThat(bill.getPurchaseOperations()).containsOnly(purchaseOperationBack);
        assertThat(purchaseOperationBack.getBill()).isEqualTo(bill);

        bill.setPurchaseOperations(new HashSet<>());
        assertThat(bill.getPurchaseOperations()).doesNotContain(purchaseOperationBack);
        assertThat(purchaseOperationBack.getBill()).isNull();
    }
}
