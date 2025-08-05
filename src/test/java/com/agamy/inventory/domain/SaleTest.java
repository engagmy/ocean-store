package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.ProductTestSamples.*;
import static com.agamy.inventory.domain.SaleOperationTestSamples.*;
import static com.agamy.inventory.domain.SalePaymentTestSamples.*;
import static com.agamy.inventory.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sale.class);
        Sale sale1 = getSaleSample1();
        Sale sale2 = new Sale();
        assertThat(sale1).isNotEqualTo(sale2);

        sale2.setId(sale1.getId());
        assertThat(sale1).isEqualTo(sale2);

        sale2 = getSaleSample2();
        assertThat(sale1).isNotEqualTo(sale2);
    }

    @Test
    void saleOperationTest() {
        Sale sale = getSaleRandomSampleGenerator();
        SaleOperation saleOperationBack = getSaleOperationRandomSampleGenerator();

        sale.setSaleOperation(saleOperationBack);
        assertThat(sale.getSaleOperation()).isEqualTo(saleOperationBack);

        sale.saleOperation(null);
        assertThat(sale.getSaleOperation()).isNull();
    }

    @Test
    void productTest() {
        Sale sale = getSaleRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        sale.setProduct(productBack);
        assertThat(sale.getProduct()).isEqualTo(productBack);

        sale.product(null);
        assertThat(sale.getProduct()).isNull();
    }

    @Test
    void salePaymentTest() {
        Sale sale = getSaleRandomSampleGenerator();
        SalePayment salePaymentBack = getSalePaymentRandomSampleGenerator();

        sale.addSalePayment(salePaymentBack);
        assertThat(sale.getSalePayments()).containsOnly(salePaymentBack);
        assertThat(salePaymentBack.getSale()).isEqualTo(sale);

        sale.removeSalePayment(salePaymentBack);
        assertThat(sale.getSalePayments()).doesNotContain(salePaymentBack);
        assertThat(salePaymentBack.getSale()).isNull();

        sale.salePayments(new HashSet<>(Set.of(salePaymentBack)));
        assertThat(sale.getSalePayments()).containsOnly(salePaymentBack);
        assertThat(salePaymentBack.getSale()).isEqualTo(sale);

        sale.setSalePayments(new HashSet<>());
        assertThat(sale.getSalePayments()).doesNotContain(salePaymentBack);
        assertThat(salePaymentBack.getSale()).isNull();
    }
}
