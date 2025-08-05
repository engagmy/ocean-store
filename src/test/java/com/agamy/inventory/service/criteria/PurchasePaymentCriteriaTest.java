package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PurchasePaymentCriteriaTest {

    @Test
    void newPurchasePaymentCriteriaHasAllFiltersNullTest() {
        var purchasePaymentCriteria = new PurchasePaymentCriteria();
        assertThat(purchasePaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void purchasePaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var purchasePaymentCriteria = new PurchasePaymentCriteria();

        setAllFilters(purchasePaymentCriteria);

        assertThat(purchasePaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void purchasePaymentCriteriaCopyCreatesNullFilterTest() {
        var purchasePaymentCriteria = new PurchasePaymentCriteria();
        var copy = purchasePaymentCriteria.copy();

        assertThat(purchasePaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(purchasePaymentCriteria)
        );
    }

    @Test
    void purchasePaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var purchasePaymentCriteria = new PurchasePaymentCriteria();
        setAllFilters(purchasePaymentCriteria);

        var copy = purchasePaymentCriteria.copy();

        assertThat(purchasePaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(purchasePaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var purchasePaymentCriteria = new PurchasePaymentCriteria();

        assertThat(purchasePaymentCriteria).hasToString("PurchasePaymentCriteria{}");
    }

    private static void setAllFilters(PurchasePaymentCriteria purchasePaymentCriteria) {
        purchasePaymentCriteria.id();
        purchasePaymentCriteria.date();
        purchasePaymentCriteria.amount();
        purchasePaymentCriteria.active();
        purchasePaymentCriteria.createdBy();
        purchasePaymentCriteria.createdDate();
        purchasePaymentCriteria.lastModifiedBy();
        purchasePaymentCriteria.lastModifiedDate();
        purchasePaymentCriteria.purchaseId();
        purchasePaymentCriteria.distinct();
    }

    private static Condition<PurchasePaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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
                condition.apply(criteria.getPurchaseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PurchasePaymentCriteria> copyFiltersAre(
        PurchasePaymentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
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
                condition.apply(criteria.getPurchaseId(), copy.getPurchaseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
