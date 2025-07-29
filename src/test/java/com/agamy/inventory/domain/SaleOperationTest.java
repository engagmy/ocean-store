package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.BillTestSamples.*;
import static com.agamy.inventory.domain.CustomerTestSamples.*;
import static com.agamy.inventory.domain.SaleOperationTestSamples.*;
import static com.agamy.inventory.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleOperationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOperation.class);
        SaleOperation saleOperation1 = getSaleOperationSample1();
        SaleOperation saleOperation2 = new SaleOperation();
        assertThat(saleOperation1).isNotEqualTo(saleOperation2);

        saleOperation2.setId(saleOperation1.getId());
        assertThat(saleOperation1).isEqualTo(saleOperation2);

        saleOperation2 = getSaleOperationSample2();
        assertThat(saleOperation1).isNotEqualTo(saleOperation2);
    }

    @Test
    void billTest() {
        SaleOperation saleOperation = getSaleOperationRandomSampleGenerator();
        Bill billBack = getBillRandomSampleGenerator();

        saleOperation.setBill(billBack);
        assertThat(saleOperation.getBill()).isEqualTo(billBack);

        saleOperation.bill(null);
        assertThat(saleOperation.getBill()).isNull();
    }

    @Test
    void customerTest() {
        SaleOperation saleOperation = getSaleOperationRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        saleOperation.setCustomer(customerBack);
        assertThat(saleOperation.getCustomer()).isEqualTo(customerBack);

        saleOperation.customer(null);
        assertThat(saleOperation.getCustomer()).isNull();
    }

    @Test
    void saleTest() {
        SaleOperation saleOperation = getSaleOperationRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        saleOperation.addSale(saleBack);
        assertThat(saleOperation.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getSaleOperation()).isEqualTo(saleOperation);

        saleOperation.removeSale(saleBack);
        assertThat(saleOperation.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getSaleOperation()).isNull();

        saleOperation.sales(new HashSet<>(Set.of(saleBack)));
        assertThat(saleOperation.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getSaleOperation()).isEqualTo(saleOperation);

        saleOperation.setSales(new HashSet<>());
        assertThat(saleOperation.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getSaleOperation()).isNull();
    }
}
