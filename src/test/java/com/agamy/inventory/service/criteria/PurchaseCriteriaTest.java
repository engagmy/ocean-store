package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PurchaseCriteriaTest {

    @Test
    void newPurchaseCriteriaHasAllFiltersNullTest() {
        var purchaseCriteria = new PurchaseCriteria();
        assertThat(purchaseCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void purchaseCriteriaFluentMethodsCreatesFiltersTest() {
        var purchaseCriteria = new PurchaseCriteria();

        setAllFilters(purchaseCriteria);

        assertThat(purchaseCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void purchaseCriteriaCopyCreatesNullFilterTest() {
        var purchaseCriteria = new PurchaseCriteria();
        var copy = purchaseCriteria.copy();

        assertThat(purchaseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(purchaseCriteria)
        );
    }

    @Test
    void purchaseCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var purchaseCriteria = new PurchaseCriteria();
        setAllFilters(purchaseCriteria);

        var copy = purchaseCriteria.copy();

        assertThat(purchaseCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(purchaseCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var purchaseCriteria = new PurchaseCriteria();

        assertThat(purchaseCriteria).hasToString("PurchaseCriteria{}");
    }

    private static void setAllFilters(PurchaseCriteria purchaseCriteria) {
        purchaseCriteria.id();
        purchaseCriteria.productName();
        purchaseCriteria.quantity();
        purchaseCriteria.unitPrice();
        purchaseCriteria.lineTotal();
        purchaseCriteria.active();
        purchaseCriteria.createdBy();
        purchaseCriteria.createdDate();
        purchaseCriteria.lastModifiedBy();
        purchaseCriteria.lastModifiedDate();
        purchaseCriteria.purchaseOperationId();
        purchaseCriteria.productId();
        purchaseCriteria.supplierId();
        purchaseCriteria.purchasePaymentId();
        purchaseCriteria.distinct();
    }

    private static Condition<PurchaseCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getProductName()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getUnitPrice()) &&
                condition.apply(criteria.getLineTotal()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getPurchaseOperationId()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getPurchasePaymentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PurchaseCriteria> copyFiltersAre(PurchaseCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getProductName(), copy.getProductName()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getUnitPrice(), copy.getUnitPrice()) &&
                condition.apply(criteria.getLineTotal(), copy.getLineTotal()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getPurchaseOperationId(), copy.getPurchaseOperationId()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getPurchasePaymentId(), copy.getPurchasePaymentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
