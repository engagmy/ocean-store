package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.InventoryTransaction;
import com.agamy.inventory.repository.InventoryTransactionRepository;
import com.agamy.inventory.service.criteria.InventoryTransactionCriteria;
import com.agamy.inventory.service.dto.InventoryTransactionDTO;
import com.agamy.inventory.service.mapper.InventoryTransactionMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link InventoryTransaction} entities in the database.
 * The main input is a {@link InventoryTransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InventoryTransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InventoryTransactionQueryService extends QueryService<InventoryTransaction> {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryTransactionQueryService.class);

    private final InventoryTransactionRepository inventoryTransactionRepository;

    private final InventoryTransactionMapper inventoryTransactionMapper;

    public InventoryTransactionQueryService(
        InventoryTransactionRepository inventoryTransactionRepository,
        InventoryTransactionMapper inventoryTransactionMapper
    ) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
    }

    /**
     * Return a {@link Page} of {@link InventoryTransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> findByCriteria(InventoryTransactionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InventoryTransaction> specification = createSpecification(criteria);
        return inventoryTransactionRepository.findAll(specification, page).map(inventoryTransactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InventoryTransactionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InventoryTransaction> specification = createSpecification(criteria);
        return inventoryTransactionRepository.count(specification);
    }

    /**
     * Function to convert {@link InventoryTransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InventoryTransaction> createSpecification(InventoryTransactionCriteria criteria) {
        Specification<InventoryTransaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), InventoryTransaction_.id),
                buildRangeSpecification(criteria.getDate(), InventoryTransaction_.date),
                buildSpecification(criteria.getType(), InventoryTransaction_.type),
                buildRangeSpecification(criteria.getQuantity(), InventoryTransaction_.quantity),
                buildSpecification(criteria.getActive(), InventoryTransaction_.active),
                buildStringSpecification(criteria.getCreatedBy(), InventoryTransaction_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), InventoryTransaction_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), InventoryTransaction_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), InventoryTransaction_.lastModifiedDate),
                buildSpecification(criteria.getProductId(), root -> root.join(InventoryTransaction_.product, JoinType.LEFT).get(Product_.id)
                )
            );
        }
        return specification;
    }
}
