package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleCriteriaTest {

    @Test
    void newSaleCriteriaHasAllFiltersNullTest() {
        var saleCriteria = new SaleCriteria();
        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleCriteriaFluentMethodsCreatesFiltersTest() {
        var saleCriteria = new SaleCriteria();

        setAllFilters(saleCriteria);

        assertThat(saleCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleCriteriaCopyCreatesNullFilterTest() {
        var saleCriteria = new SaleCriteria();
        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void saleCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleCriteria = new SaleCriteria();
        setAllFilters(saleCriteria);

        var copy = saleCriteria.copy();

        assertThat(saleCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleCriteria = new SaleCriteria();

        assertThat(saleCriteria).hasToString("SaleCriteria{}");
    }

    private static void setAllFilters(SaleCriteria saleCriteria) {
        saleCriteria.id();
        saleCriteria.productName();
        saleCriteria.quantity();
        saleCriteria.unitPrice();
        saleCriteria.discount();
        saleCriteria.lineTotal();
        saleCriteria.active();
        saleCriteria.createdBy();
        saleCriteria.createdDate();
        saleCriteria.lastModifiedBy();
        saleCriteria.lastModifiedDate();
        saleCriteria.saleOperationId();
        saleCriteria.productId();
        saleCriteria.salePaymentId();
        saleCriteria.distinct();
    }

    private static Condition<SaleCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProductName()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getDiscount()) &&
                condition.apply(criteria.getLineTotal()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleOperationId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getSalePaymentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleCriteria> copyFiltersAre(SaleCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProductName(), copy.getProductName()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getDiscount(), copy.getDiscount()) &&
                condition.apply(criteria.getLineTotal(), copy.getLineTotal()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleOperationId(), copy.getSaleOperationId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getSalePaymentId(), copy.getSalePaymentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
