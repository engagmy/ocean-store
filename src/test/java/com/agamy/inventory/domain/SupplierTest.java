package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.PurchaseTestSamples.*;
import static com.agamy.inventory.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SupplierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Supplier.class);
        Supplier supplier1 = getSupplierSample1();
        Supplier supplier2 = new Supplier();
        assertThat(supplier1).isNotEqualTo(supplier2);

        supplier2.setId(supplier1.getId());
        assertThat(supplier1).isEqualTo(supplier2);

        supplier2 = getSupplierSample2();
        assertThat(supplier1).isNotEqualTo(supplier2);
    }

    @Test
    void purchaseTest() {
        Supplier supplier = getSupplierRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        supplier.addPurchase(purchaseBack);
        assertThat(supplier.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getSupplier()).isEqualTo(supplier);

        supplier.removePurchase(purchaseBack);
        assertThat(supplier.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getSupplier()).isNull();

        supplier.purchases(new HashSet<>(Set.of(purchaseBack)));
        assertThat(supplier.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getSupplier()).isEqualTo(supplier);

        supplier.setPurchases(new HashSet<>());
        assertThat(supplier.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getSupplier()).isNull();
    }
}
