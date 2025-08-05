package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.BrandTestSamples.*;
import static com.agamy.inventory.domain.ProductTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BrandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = getBrandSample1();
        Brand brand2 = new Brand();
        assertThat(brand1).isNotEqualTo(brand2);

        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);

        brand2 = getBrandSample2();
        assertThat(brand1).isNotEqualTo(brand2);
    }

    @Test
    void productTest() {
        Brand brand = getBrandRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        brand.addProduct(productBack);
        assertThat(brand.getProducts()).containsOnly(productBack);
        assertThat(productBack.getBrand()).isEqualTo(brand);

        brand.removeProduct(productBack);
        assertThat(brand.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getBrand()).isNull();

        brand.products(new HashSet<>(Set.of(productBack)));
        assertThat(brand.getProducts()).containsOnly(productBack);
        assertThat(productBack.getBrand()).isEqualTo(brand);

        brand.setProducts(new HashSet<>());
        assertThat(brand.getProducts()).doesNotContain(productBack);
        assertThat(productBack.getBrand()).isNull();
    }
}
