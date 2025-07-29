package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BillCriteriaTest {

    @Test
    void newBillCriteriaHasAllFiltersNullTest() {
        var billCriteria = new BillCriteria();
        assertThat(billCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void billCriteriaFluentMethodsCreatesFiltersTest() {
        var billCriteria = new BillCriteria();

        setAllFilters(billCriteria);

        assertThat(billCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void billCriteriaCopyCreatesNullFilterTest() {
        var billCriteria = new BillCriteria();
        var copy = billCriteria.copy();

        assertThat(billCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(billCriteria)
        );
    }

    @Test
    void billCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var billCriteria = new BillCriteria();
        setAllFilters(billCriteria);

        var copy = billCriteria.copy();

        assertThat(billCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(billCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var billCriteria = new BillCriteria();

        assertThat(billCriteria).hasToString("BillCriteria{}");
    }

    private static void setAllFilters(BillCriteria billCriteria) {
        billCriteria.id();
        billCriteria.billNumber();
        billCriteria.date();
        billCriteria.totalAmount();
        billCriteria.taxAmount();
        billCriteria.discountAmount();
        billCriteria.paidAmount();
        billCriteria.dueAmount();
        billCriteria.notes();
        billCriteria.active();
        billCriteria.createdBy();
        billCriteria.createdDate();
        billCriteria.lastModifiedBy();
        billCriteria.lastModifiedDate();
        billCriteria.saleOperationId();
        billCriteria.purchaseOperationId();
        billCriteria.distinct();
    }

    private static Condition<BillCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getBillNumber()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getTaxAmount()) &&
                condition.apply(criteria.getDiscountAmount()) &&
                condition.apply(criteria.getPaidAmount()) &&
                condition.apply(criteria.getDueAmount()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleOperationId()) &&
                condition.apply(criteria.getPurchaseOperationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BillCriteria> copyFiltersAre(BillCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getBillNumber(), copy.getBillNumber()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getTaxAmount(), copy.getTaxAmount()) &&
                condition.apply(criteria.getDiscountAmount(), copy.getDiscountAmount()) &&
                condition.apply(criteria.getPaidAmount(), copy.getPaidAmount()) &&
                condition.apply(criteria.getDueAmount(), copy.getDueAmount()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getSaleOperationId(), copy.getSaleOperationId()) &&
                condition.apply(criteria.getPurchaseOperationId(), copy.getPurchaseOperationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
