package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SalaryPaymentCriteriaTest {

    @Test
    void newSalaryPaymentCriteriaHasAllFiltersNullTest() {
        var salaryPaymentCriteria = new SalaryPaymentCriteria();
        assertThat(salaryPaymentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void salaryPaymentCriteriaFluentMethodsCreatesFiltersTest() {
        var salaryPaymentCriteria = new SalaryPaymentCriteria();

        setAllFilters(salaryPaymentCriteria);

        assertThat(salaryPaymentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void salaryPaymentCriteriaCopyCreatesNullFilterTest() {
        var salaryPaymentCriteria = new SalaryPaymentCriteria();
        var copy = salaryPaymentCriteria.copy();

        assertThat(salaryPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(salaryPaymentCriteria)
        );
    }

    @Test
    void salaryPaymentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var salaryPaymentCriteria = new SalaryPaymentCriteria();
        setAllFilters(salaryPaymentCriteria);

        var copy = salaryPaymentCriteria.copy();

        assertThat(salaryPaymentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(salaryPaymentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var salaryPaymentCriteria = new SalaryPaymentCriteria();

        assertThat(salaryPaymentCriteria).hasToString("SalaryPaymentCriteria{}");
    }

    private static void setAllFilters(SalaryPaymentCriteria salaryPaymentCriteria) {
        salaryPaymentCriteria.id();
        salaryPaymentCriteria.date();
        salaryPaymentCriteria.amount();
        salaryPaymentCriteria.active();
        salaryPaymentCriteria.createdBy();
        salaryPaymentCriteria.createdDate();
        salaryPaymentCriteria.lastModifiedBy();
        salaryPaymentCriteria.lastModifiedDate();
        salaryPaymentCriteria.employeeId();
        salaryPaymentCriteria.distinct();
    }

    private static Condition<SalaryPaymentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
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
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<SalaryPaymentCriteria> copyFiltersAre(
        SalaryPaymentCriteria copy,
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
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
