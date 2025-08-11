package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SalePaymentCriteriaTest {

    @Test
    void newSalePaymentCriteriaHasAllFiltersNullTest() {
        var salePaymentCriteria = new SalePaymentCriteria();
        assertThat(salePaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void salePaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var salePaymentCriteria = new SalePaymentCriteria();

        setAllFilters(salePaymentCriteria);

        assertThat(salePaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void salePaymentCriteriaCopyCreatesNullFilterTest() {
        var salePaymentCriteria = new SalePaymentCriteria();
        var copy = salePaymentCriteria.copy();

        assertThat(salePaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(salePaymentCriteria)
        );
    }

    @Test
    void salePaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var salePaymentCriteria = new SalePaymentCriteria();
        setAllFilters(salePaymentCriteria);

        var copy = salePaymentCriteria.copy();

        assertThat(salePaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(salePaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var salePaymentCriteria = new SalePaymentCriteria();

        assertThat(salePaymentCriteria).hasToString("SalePaymentCriteria{}");
    }

    private static void setAllFilters(SalePaymentCriteria salePaymentCriteria) {
        salePaymentCriteria.id();
        salePaymentCriteria.date();
        salePaymentCriteria.amount();
        salePaymentCriteria.active();
        salePaymentCriteria.createdBy();
        salePaymentCriteria.createdDate();
        salePaymentCriteria.lastModifiedBy();
        salePaymentCriteria.lastModifiedDate();
        salePaymentCriteria.saleId();
        salePaymentCriteria.distinct();
    }

    private static Condition<SalePaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SalePaymentCriteria> copyFiltersAre(SalePaymentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleId(), copy.getSaleId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
