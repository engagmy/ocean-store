package com.agamy.inventory.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InventoryTransactionCriteriaTest {

    @Test
    void newInventoryTransactionCriteriaHasAllFiltersNullTest() {
        var inventoryTransactionCriteria = new InventoryTransactionCriteria();
        assertThat(inventoryTransactionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void inventoryTransactionCriteriaFluentMethodsCreatesFiltersTest() {
        var inventoryTransactionCriteria = new InventoryTransactionCriteria();

        setAllFilters(inventoryTransactionCriteria);

        assertThat(inventoryTransactionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void inventoryTransactionCriteriaCopyCreatesNullFilterTest() {
        var inventoryTransactionCriteria = new InventoryTransactionCriteria();
        var copy = inventoryTransactionCriteria.copy();

        assertThat(inventoryTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryTransactionCriteria)
        );
    }

    @Test
    void inventoryTransactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var inventoryTransactionCriteria = new InventoryTransactionCriteria();
        setAllFilters(inventoryTransactionCriteria);

        var copy = inventoryTransactionCriteria.copy();

        assertThat(inventoryTransactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryTransactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var inventoryTransactionCriteria = new InventoryTransactionCriteria();

        assertThat(inventoryTransactionCriteria).hasToString("InventoryTransactionCriteria{}");
    }

    private static void setAllFilters(InventoryTransactionCriteria inventoryTransactionCriteria) {
        inventoryTransactionCriteria.id();
        inventoryTransactionCriteria.date();
        inventoryTransactionCriteria.type();
        inventoryTransactionCriteria.quantity();
        inventoryTransactionCriteria.active();
        inventoryTransactionCriteria.createdBy();
        inventoryTransactionCriteria.createdDate();
        inventoryTransactionCriteria.lastModifiedBy();
        inventoryTransactionCriteria.lastModifiedDate();
        inventoryTransactionCriteria.productId();
        inventoryTransactionCriteria.distinct();
    }

    private static Condition<InventoryTransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getDate()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getQuantity()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate()) &&
                condition.apply(criteria.getProductId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InventoryTransactionCriteria> copyFiltersAre(
        InventoryTransactionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getDate(), copy.getDate()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getQuantity(), copy.getQuantity()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getCreatedDate(), copy.getCreatedDate()) &&
                condition.apply(criteria.getLastModifiedBy(), copy.getLastModifiedBy()) &&
                condition.apply(criteria.getLastModifiedDate(), copy.getLastModifiedDate()) &&
                condition.apply(criteria.getProductId(), copy.getProductId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
