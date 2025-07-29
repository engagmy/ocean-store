package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.BrandTestSamples.*;
import static com.agamy.inventory.domain.InventoryTransactionTestSamples.*;
import static com.agamy.inventory.domain.ProductCategoryTestSamples.*;
import static com.agamy.inventory.domain.ProductTestSamples.*;
import static com.agamy.inventory.domain.PurchaseTestSamples.*;
import static com.agamy.inventory.domain.SaleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void brandTest() {
        Product product = getProductRandomSampleGenerator();
        Brand brandBack = getBrandRandomSampleGenerator();

        product.setBrand(brandBack);
        assertThat(product.getBrand()).isEqualTo(brandBack);

        product.brand(null);
        assertThat(product.getBrand()).isNull();
    }

    @Test
    void productCategoryTest() {
        Product product = getProductRandomSampleGenerator();
        ProductCategory productCategoryBack = getProductCategoryRandomSampleGenerator();

        product.setProductCategory(productCategoryBack);
        assertThat(product.getProductCategory()).isEqualTo(productCategoryBack);

        product.productCategory(null);
        assertThat(product.getProductCategory()).isNull();
    }

    @Test
    void inventoryTransactionTest() {
        Product product = getProductRandomSampleGenerator();
        InventoryTransaction inventoryTransactionBack = getInventoryTransactionRandomSampleGenerator();

        product.addInventoryTransaction(inventoryTransactionBack);
        assertThat(product.getInventoryTransactions()).containsOnly(inventoryTransactionBack);
        assertThat(inventoryTransactionBack.getProduct()).isEqualTo(product);

        product.removeInventoryTransaction(inventoryTransactionBack);
        assertThat(product.getInventoryTransactions()).doesNotContain(inventoryTransactionBack);
        assertThat(inventoryTransactionBack.getProduct()).isNull();

        product.inventoryTransactions(new HashSet<>(Set.of(inventoryTransactionBack)));
        assertThat(product.getInventoryTransactions()).containsOnly(inventoryTransactionBack);
        assertThat(inventoryTransactionBack.getProduct()).isEqualTo(product);

        product.setInventoryTransactions(new HashSet<>());
        assertThat(product.getInventoryTransactions()).doesNotContain(inventoryTransactionBack);
        assertThat(inventoryTransactionBack.getProduct()).isNull();
    }

    @Test
    void saleTest() {
        Product product = getProductRandomSampleGenerator();
        Sale saleBack = getSaleRandomSampleGenerator();

        product.addSale(saleBack);
        assertThat(product.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getProduct()).isEqualTo(product);

        product.removeSale(saleBack);
        assertThat(product.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getProduct()).isNull();

        product.sales(new HashSet<>(Set.of(saleBack)));
        assertThat(product.getSales()).containsOnly(saleBack);
        assertThat(saleBack.getProduct()).isEqualTo(product);

        product.setSales(new HashSet<>());
        assertThat(product.getSales()).doesNotContain(saleBack);
        assertThat(saleBack.getProduct()).isNull();
    }

    @Test
    void purchaseTest() {
        Product product = getProductRandomSampleGenerator();
        Purchase purchaseBack = getPurchaseRandomSampleGenerator();

        product.addPurchase(purchaseBack);
        assertThat(product.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getProduct()).isEqualTo(product);

        product.removePurchase(purchaseBack);
        assertThat(product.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getProduct()).isNull();

        product.purchases(new HashSet<>(Set.of(purchaseBack)));
        assertThat(product.getPurchases()).containsOnly(purchaseBack);
        assertThat(purchaseBack.getProduct()).isEqualTo(product);

        product.setPurchases(new HashSet<>());
        assertThat(product.getPurchases()).doesNotContain(purchaseBack);
        assertThat(purchaseBack.getProduct()).isNull();
    }
}
