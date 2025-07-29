package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PurchaseOperationCriteriaTest {

    @Test
    void newPurchaseOperationCriteriaHasAllFiltersNullTest() {
        var purchaseOperationCriteria = new PurchaseOperationCriteria();
        assertThat(purchaseOperationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void purchaseOperationCriteriaFluentMethodsCreatesFiltersTest() {
        var purchaseOperationCriteria = new PurchaseOperationCriteria();

        setAllFilters(purchaseOperationCriteria);

        assertThat(purchaseOperationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void purchaseOperationCriteriaCopyCreatesNullFilterTest() {
        var purchaseOperationCriteria = new PurchaseOperationCriteria();
        var copy = purchaseOperationCriteria.copy();

        assertThat(purchaseOperationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(purchaseOperationCriteria)
        );
    }

    @Test
    void purchaseOperationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var purchaseOperationCriteria = new PurchaseOperationCriteria();
        setAllFilters(purchaseOperationCriteria);

        var copy = purchaseOperationCriteria.copy();

        assertThat(purchaseOperationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(purchaseOperationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var purchaseOperationCriteria = new PurchaseOperationCriteria();

        assertThat(purchaseOperationCriteria).hasToString("PurchaseOperationCriteria{}");
    }

    private static void setAllFilters(PurchaseOperationCriteria purchaseOperationCriteria) {
        purchaseOperationCriteria.id();
        purchaseOperationCriteria.date();
        purchaseOperationCriteria.supplierInvoiceNo();
        purchaseOperationCriteria.totalQuantity();
        purchaseOperationCriteria.totalAmount();
        purchaseOperationCriteria.grandTotal();
        purchaseOperationCriteria.active();
        purchaseOperationCriteria.createdBy();
        purchaseOperationCriteria.createdDate();
        purchaseOperationCriteria.lastModifiedBy();
        purchaseOperationCriteria.lastModifiedDate();
        purchaseOperationCriteria.billId();
        purchaseOperationCriteria.purchaseId();
        purchaseOperationCriteria.distinct();
    }

    private static Condition<PurchaseOperationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getSupplierInvoiceNo()) &&
                condition.apply(criteria.getTotalQuantity()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getGrandTotal()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getBillId()) &&
                condition.apply(criteria.getPurchaseId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PurchaseOperationCriteria> copyFiltersAre(
        PurchaseOperationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getSupplierInvoiceNo(), copy.getSupplierInvoiceNo()) &&
                condition.apply(criteria.getTotalQuantity(), copy.getTotalQuantity()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getGrandTotal(), copy.getGrandTotal()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getBillId(), copy.getBillId()) &&
                condition.apply(criteria.getPurchaseId(), copy.getPurchaseId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
