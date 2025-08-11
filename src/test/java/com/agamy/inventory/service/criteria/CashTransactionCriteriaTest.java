package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CashTransactionCriteriaTest {

    @Test
    void newCashTransactionCriteriaHasAllFiltersNullTest() {
        var cashTransactionCriteria = new CashTransactionCriteria();
        assertThat(cashTransactionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cashTransactionCriteriaFluentMethodsCreatesFiltersTest() {
        var cashTransactionCriteria = new CashTransactionCriteria();

        setAllFilters(cashTransactionCriteria);

        assertThat(cashTransactionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cashTransactionCriteriaCopyCreatesNullFilterTest() {
        var cashTransactionCriteria = new CashTransactionCriteria();
        var copy = cashTransactionCriteria.copy();

        assertThat(cashTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cashTransactionCriteria)
        );
    }

    @Test
    void cashTransactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cashTransactionCriteria = new CashTransactionCriteria();
        setAllFilters(cashTransactionCriteria);

        var copy = cashTransactionCriteria.copy();

        assertThat(cashTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cashTransactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cashTransactionCriteria = new CashTransactionCriteria();

        assertThat(cashTransactionCriteria).hasToString("CashTransactionCriteria{}");
    }

    private static void setAllFilters(CashTransactionCriteria cashTransactionCriteria) {
        cashTransactionCriteria.id();
        cashTransactionCriteria.date();
        cashTransactionCriteria.amount();
        cashTransactionCriteria.type();
        cashTransactionCriteria.reason();
        cashTransactionCriteria.active();
        cashTransactionCriteria.createdBy();
        cashTransactionCriteria.createdDate();
        cashTransactionCriteria.lastModifiedBy();
        cashTransactionCriteria.lastModifiedDate();
        cashTransactionCriteria.distinct();
    }

    private static Condition<CashTransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CashTransactionCriteria> copyFiltersAre(
        CashTransactionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
