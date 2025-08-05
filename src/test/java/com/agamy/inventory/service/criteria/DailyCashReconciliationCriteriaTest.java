package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DailyCashReconciliationCriteriaTest {

    @Test
    void newDailyCashReconciliationCriteriaHasAllFiltersNullTest() {
        var dailyCashReconciliationCriteria = new DailyCashReconciliationCriteria();
        assertThat(dailyCashReconciliationCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void dailyCashReconciliationCriteriaFluentMethodsCreatesFiltersTest() {
        var dailyCashReconciliationCriteria = new DailyCashReconciliationCriteria();

        setAllFilters(dailyCashReconciliationCriteria);

        assertThat(dailyCashReconciliationCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void dailyCashReconciliationCriteriaCopyCreatesNullFilterTest() {
        var dailyCashReconciliationCriteria = new DailyCashReconciliationCriteria();
        var copy = dailyCashReconciliationCriteria.copy();

        assertThat(dailyCashReconciliationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(dailyCashReconciliationCriteria)
        );
    }

    @Test
    void dailyCashReconciliationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var dailyCashReconciliationCriteria = new DailyCashReconciliationCriteria();
        setAllFilters(dailyCashReconciliationCriteria);

        var copy = dailyCashReconciliationCriteria.copy();

        assertThat(dailyCashReconciliationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(dailyCashReconciliationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var dailyCashReconciliationCriteria = new DailyCashReconciliationCriteria();

        assertThat(dailyCashReconciliationCriteria).hasToString("DailyCashReconciliationCriteria{}");
    }

    private static void setAllFilters(DailyCashReconciliationCriteria dailyCashReconciliationCriteria) {
        dailyCashReconciliationCriteria.id();
        dailyCashReconciliationCriteria.date();
        dailyCashReconciliationCriteria.openingBalance();
        dailyCashReconciliationCriteria.totalSales();
        dailyCashReconciliationCriteria.totalPurchases();
        dailyCashReconciliationCriteria.totalSalaryPaid();
        dailyCashReconciliationCriteria.ownerDeposits();
        dailyCashReconciliationCriteria.withdrawals();
        dailyCashReconciliationCriteria.closingBalance();
        dailyCashReconciliationCriteria.notes();
        dailyCashReconciliationCriteria.active();
        dailyCashReconciliationCriteria.createdBy();
        dailyCashReconciliationCriteria.createdDate();
        dailyCashReconciliationCriteria.lastModifiedBy();
        dailyCashReconciliationCriteria.lastModifiedDate();
        dailyCashReconciliationCriteria.dailyCashDetailId();
        dailyCashReconciliationCriteria.distinct();
    }

    private static Condition<DailyCashReconciliationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getOpeningBalance()) &&
                condition.apply(criteria.getTotalSales()) &&
                condition.apply(criteria.getTotalPurchases()) &&
                condition.apply(criteria.getTotalSalaryPaid()) &&
                condition.apply(criteria.getOwnerDeposits()) &&
                condition.apply(criteria.getWithdrawals()) &&
                condition.apply(criteria.getClosingBalance()) &&
                condition.apply(criteria.getNotes()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getDailyCashDetailId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DailyCashReconciliationCriteria> copyFiltersAre(
        DailyCashReconciliationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getOpeningBalance(), copy.getOpeningBalance()) &&
                condition.apply(criteria.getTotalSales(), copy.getTotalSales()) &&
                condition.apply(criteria.getTotalPurchases(), copy.getTotalPurchases()) &&
                condition.apply(criteria.getTotalSalaryPaid(), copy.getTotalSalaryPaid()) &&
                condition.apply(criteria.getOwnerDeposits(), copy.getOwnerDeposits()) &&
                condition.apply(criteria.getWithdrawals(), copy.getWithdrawals()) &&
                condition.apply(criteria.getClosingBalance(), copy.getClosingBalance()) &&
                condition.apply(criteria.getNotes(), copy.getNotes()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getDailyCashDetailId(), copy.getDailyCashDetailId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
