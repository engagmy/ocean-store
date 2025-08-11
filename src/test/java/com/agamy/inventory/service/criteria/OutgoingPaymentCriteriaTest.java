package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OutgoingPaymentCriteriaTest {

    @Test
    void newOutgoingPaymentCriteriaHasAllFiltersNullTest() {
        var outgoingPaymentCriteria = new OutgoingPaymentCriteria();
        assertThat(outgoingPaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void outgoingPaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var outgoingPaymentCriteria = new OutgoingPaymentCriteria();

        setAllFilters(outgoingPaymentCriteria);

        assertThat(outgoingPaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void outgoingPaymentCriteriaCopyCreatesNullFilterTest() {
        var outgoingPaymentCriteria = new OutgoingPaymentCriteria();
        var copy = outgoingPaymentCriteria.copy();

        assertThat(outgoingPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(outgoingPaymentCriteria)
        );
    }

    @Test
    void outgoingPaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var outgoingPaymentCriteria = new OutgoingPaymentCriteria();
        setAllFilters(outgoingPaymentCriteria);

        var copy = outgoingPaymentCriteria.copy();

        assertThat(outgoingPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(outgoingPaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var outgoingPaymentCriteria = new OutgoingPaymentCriteria();

        assertThat(outgoingPaymentCriteria).hasToString("OutgoingPaymentCriteria{}");
    }

    private static void setAllFilters(OutgoingPaymentCriteria outgoingPaymentCriteria) {
        outgoingPaymentCriteria.id();
        outgoingPaymentCriteria.date();
        outgoingPaymentCriteria.amount();
        outgoingPaymentCriteria.reason();
        outgoingPaymentCriteria.notes();
        outgoingPaymentCriteria.active();
        outgoingPaymentCriteria.createdBy();
        outgoingPaymentCriteria.createdDate();
        outgoingPaymentCriteria.lastModifiedBy();
        outgoingPaymentCriteria.lastModifiedDate();
        outgoingPaymentCriteria.distinct();
    }

    private static Condition<OutgoingPaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getReason()) &&
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

    private static Condition<OutgoingPaymentCriteria> copyFiltersAre(
        OutgoingPaymentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
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
