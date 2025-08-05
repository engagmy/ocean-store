package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class SupplierCriteriaTest {

    @Test
    void newSupplierCriteriaHasAllFiltersNullTest() {
        var supplierCriteria = new SupplierCriteria();
        assertThat(supplierCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void supplierCriteriaFluentMethodsCreatesFiltersTest() {
        var supplierCriteria = new SupplierCriteria();

        setAllFilters(supplierCriteria);

        assertThat(supplierCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void supplierCriteriaCopyCreatesNullFilterTest() {
        var supplierCriteria = new SupplierCriteria();
        var copy = supplierCriteria.copy();

        assertThat(supplierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(supplierCriteria)
        );
    }

    @Test
    void supplierCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var supplierCriteria = new SupplierCriteria();
        setAllFilters(supplierCriteria);

        var copy = supplierCriteria.copy();

        assertThat(supplierCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(supplierCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var supplierCriteria = new SupplierCriteria();

        assertThat(supplierCriteria).hasToString("SupplierCriteria{}");
    }

    private static void setAllFilters(SupplierCriteria supplierCriteria) {
        supplierCriteria.id();
        supplierCriteria.name();
        supplierCriteria.phone();
        supplierCriteria.address();
        supplierCriteria.active();
        supplierCriteria.createdBy();
        supplierCriteria.createdDate();
        supplierCriteria.lastModifiedBy();
        supplierCriteria.lastModifiedDate();
        supplierCriteria.purchaseId();
        supplierCriteria.distinct();
    }

    private static Condition<SupplierCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getAddress()) &&
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

    private static Condition<SupplierCriteria> copyFiltersAre(SupplierCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
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
