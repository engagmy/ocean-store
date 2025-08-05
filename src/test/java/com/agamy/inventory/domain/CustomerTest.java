package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.CustomerTestSamples.*;
import static com.agamy.inventory.domain.SaleOperationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void saleOperationTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        SaleOperation saleOperationBack = getSaleOperationRandomSampleGenerator();

        customer.addSaleOperation(saleOperationBack);
        assertThat(customer.getSaleOperations()).containsOnly(saleOperationBack);
        assertThat(saleOperationBack.getCustomer()).isEqualTo(customer);

        customer.removeSaleOperation(saleOperationBack);
        assertThat(customer.getSaleOperations()).doesNotContain(saleOperationBack);
        assertThat(saleOperationBack.getCustomer()).isNull();

        customer.saleOperations(new HashSet<>(Set.of(saleOperationBack)));
        assertThat(customer.getSaleOperations()).containsOnly(saleOperationBack);
        assertThat(saleOperationBack.getCustomer()).isEqualTo(customer);

        customer.setSaleOperations(new HashSet<>());
        assertThat(customer.getSaleOperations()).doesNotContain(saleOperationBack);
        assertThat(saleOperationBack.getCustomer()).isNull();
    }
}
