package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CashBalanceCriteriaTest {

    @Test
    void newCashBalanceCriteriaHasAllFiltersNullTest() {
        var cashBalanceCriteria = new CashBalanceCriteria();
        assertThat(cashBalanceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cashBalanceCriteriaFluentMethodsCreatesFiltersTest() {
        var cashBalanceCriteria = new CashBalanceCriteria();

        setAllFilters(cashBalanceCriteria);

        assertThat(cashBalanceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cashBalanceCriteriaCopyCreatesNullFilterTest() {
        var cashBalanceCriteria = new CashBalanceCriteria();
        var copy = cashBalanceCriteria.copy();

        assertThat(cashBalanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cashBalanceCriteria)
        );
    }

    @Test
    void cashBalanceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cashBalanceCriteria = new CashBalanceCriteria();
        setAllFilters(cashBalanceCriteria);

        var copy = cashBalanceCriteria.copy();

        assertThat(cashBalanceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cashBalanceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cashBalanceCriteria = new CashBalanceCriteria();

        assertThat(cashBalanceCriteria).hasToString("CashBalanceCriteria{}");
    }

    private static void setAllFilters(CashBalanceCriteria cashBalanceCriteria) {
        cashBalanceCriteria.id();
        cashBalanceCriteria.available();
        cashBalanceCriteria.lastUpdated();
        cashBalanceCriteria.notes();
        cashBalanceCriteria.active();
        cashBalanceCriteria.createdBy();
        cashBalanceCriteria.createdDate();
        cashBalanceCriteria.lastModifiedBy();
        cashBalanceCriteria.lastModifiedDate();
        cashBalanceCriteria.distinct();
    }

    private static Condition<CashBalanceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAvailable()) &&
                condition.apply(criteria.getLastUpdated()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CashBalanceCriteria> copyFiltersAre(CashBalanceCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAvailable(), copy.getAvailable()) &&
                condition.apply(criteria.getLastUpdated(), copy.getLastUpdated()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
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
