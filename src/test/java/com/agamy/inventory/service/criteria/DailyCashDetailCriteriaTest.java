package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DailyCashDetailCriteriaTest {

    @Test
    void newDailyCashDetailCriteriaHasAllFiltersNullTest() {
        var dailyCashDetailCriteria = new DailyCashDetailCriteria();
        assertThat(dailyCashDetailCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void dailyCashDetailCriteriaFluentMethodsCreatesFiltersTest() {
        var dailyCashDetailCriteria = new DailyCashDetailCriteria();

        setAllFilters(dailyCashDetailCriteria);

        assertThat(dailyCashDetailCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void dailyCashDetailCriteriaCopyCreatesNullFilterTest() {
        var dailyCashDetailCriteria = new DailyCashDetailCriteria();
        var copy = dailyCashDetailCriteria.copy();

        assertThat(dailyCashDetailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(dailyCashDetailCriteria)
        );
    }

    @Test
    void dailyCashDetailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var dailyCashDetailCriteria = new DailyCashDetailCriteria();
        setAllFilters(dailyCashDetailCriteria);

        var copy = dailyCashDetailCriteria.copy();

        assertThat(dailyCashDetailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(dailyCashDetailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var dailyCashDetailCriteria = new DailyCashDetailCriteria();

        assertThat(dailyCashDetailCriteria).hasToString("DailyCashDetailCriteria{}");
    }

    private static void setAllFilters(DailyCashDetailCriteria dailyCashDetailCriteria) {
        dailyCashDetailCriteria.id();
        dailyCashDetailCriteria.type();
        dailyCashDetailCriteria.referenceId();
        dailyCashDetailCriteria.referenceType();
        dailyCashDetailCriteria.amount();
        dailyCashDetailCriteria.description();
        dailyCashDetailCriteria.timestamp();
        dailyCashDetailCriteria.active();
        dailyCashDetailCriteria.createdBy();
        dailyCashDetailCriteria.createdDate();
        dailyCashDetailCriteria.lastModifiedBy();
        dailyCashDetailCriteria.lastModifiedDate();
        dailyCashDetailCriteria.dailyCashReconciliationId();
        dailyCashDetailCriteria.distinct();
    }

    private static Condition<DailyCashDetailCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getReferenceId()) &&
                condition.apply(criteria.getReferenceType()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDailyCashReconciliationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DailyCashDetailCriteria> copyFiltersAre(
        DailyCashDetailCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getReferenceId(), copy.getReferenceId()) &&
                condition.apply(criteria.getReferenceType(), copy.getReferenceType()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDailyCashReconciliationId(), copy.getDailyCashReconciliationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
