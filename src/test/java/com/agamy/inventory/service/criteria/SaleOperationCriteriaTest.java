package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SaleOperationCriteriaTest {

    @Test
    void newSaleOperationCriteriaHasAllFiltersNullTest() {
        var saleOperationCriteria = new SaleOperationCriteria();
        assertThat(saleOperationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void saleOperationCriteriaFluentMethodsCreatesFiltersTest() {
        var saleOperationCriteria = new SaleOperationCriteria();

        setAllFilters(saleOperationCriteria);

        assertThat(saleOperationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void saleOperationCriteriaCopyCreatesNullFilterTest() {
        var saleOperationCriteria = new SaleOperationCriteria();
        var copy = saleOperationCriteria.copy();

        assertThat(saleOperationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(saleOperationCriteria)
        );
    }

    @Test
    void saleOperationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var saleOperationCriteria = new SaleOperationCriteria();
        setAllFilters(saleOperationCriteria);

        var copy = saleOperationCriteria.copy();

        assertThat(saleOperationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(saleOperationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var saleOperationCriteria = new SaleOperationCriteria();

        assertThat(saleOperationCriteria).hasToString("SaleOperationCriteria{}");
    }

    private static void setAllFilters(SaleOperationCriteria saleOperationCriteria) {
        saleOperationCriteria.id();
        saleOperationCriteria.date();
        saleOperationCriteria.totalQuantity();
        saleOperationCriteria.totalAmount();
        saleOperationCriteria.totalDiscount();
        saleOperationCriteria.grandTotal();
        saleOperationCriteria.active();
        saleOperationCriteria.createdBy();
        saleOperationCriteria.createdDate();
        saleOperationCriteria.lastModifiedBy();
        saleOperationCriteria.lastModifiedDate();
        saleOperationCriteria.billId();
        saleOperationCriteria.customerId();
        saleOperationCriteria.saleId();
        saleOperationCriteria.distinct();
    }

    private static Condition<SaleOperationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getTotalQuantity()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getTotalDiscount()) &&
                condition.apply(criteria.getGrandTotal()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getBillId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getSaleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SaleOperationCriteria> copyFiltersAre(
        SaleOperationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getTotalQuantity(), copy.getTotalQuantity()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getTotalDiscount(), copy.getTotalDiscount()) &&
                condition.apply(criteria.getGrandTotal(), copy.getGrandTotal()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getBillId(), copy.getBillId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getSaleId(), copy.getSaleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
