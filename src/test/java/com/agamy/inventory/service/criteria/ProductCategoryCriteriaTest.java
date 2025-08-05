package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProductCategoryCriteriaTest {

    @Test
    void newProductCategoryCriteriaHasAllFiltersNullTest() {
        var productCategoryCriteria = new ProductCategoryCriteria();
        assertThat(productCategoryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void productCategoryCriteriaFluentMethodsCreatesFiltersTest() {
        var productCategoryCriteria = new ProductCategoryCriteria();

        setAllFilters(productCategoryCriteria);

        assertThat(productCategoryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void productCategoryCriteriaCopyCreatesNullFilterTest() {
        var productCategoryCriteria = new ProductCategoryCriteria();
        var copy = productCategoryCriteria.copy();

        assertThat(productCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(productCategoryCriteria)
        );
    }

    @Test
    void productCategoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var productCategoryCriteria = new ProductCategoryCriteria();
        setAllFilters(productCategoryCriteria);

        var copy = productCategoryCriteria.copy();

        assertThat(productCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(productCategoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var productCategoryCriteria = new ProductCategoryCriteria();

        assertThat(productCategoryCriteria).hasToString("ProductCategoryCriteria{}");
    }

    private static void setAllFilters(ProductCategoryCriteria productCategoryCriteria) {
        productCategoryCriteria.id();
        productCategoryCriteria.name();
        productCategoryCriteria.active();
        productCategoryCriteria.createdBy();
        productCategoryCriteria.createdDate();
        productCategoryCriteria.lastModifiedBy();
        productCategoryCriteria.lastModifiedDate();
        productCategoryCriteria.productId();
        productCategoryCriteria.distinct();
    }

    private static Condition<ProductCategoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProductCategoryCriteria> copyFiltersAre(
        ProductCategoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
