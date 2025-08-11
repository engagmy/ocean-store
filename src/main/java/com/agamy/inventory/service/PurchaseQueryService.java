package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.Purchase;
import com.agamy.inventory.repository.PurchaseRepository;
import com.agamy.inventory.service.criteria.PurchaseCriteria;
import com.agamy.inventory.service.dto.PurchaseDTO;
import com.agamy.inventory.service.mapper.PurchaseMapper;
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
 * Service for executing complex queries for {@link Purchase} entities in the database.
 * The main input is a {@link PurchaseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PurchaseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PurchaseQueryService extends QueryService<Purchase> {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseQueryService.class);

    private final PurchaseRepository purchaseRepository;

    private final PurchaseMapper purchaseMapper;

    public PurchaseQueryService(PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    /**
     * Return a {@link Page} of {@link PurchaseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseDTO> findByCriteria(PurchaseCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Purchase> specification = createSpecification(criteria);
        return purchaseRepository.findAll(specification, page).map(purchaseMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PurchaseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Purchase> specification = createSpecification(criteria);
        return purchaseRepository.count(specification);
    }

    /**
     * Function to convert {@link PurchaseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Purchase> createSpecification(PurchaseCriteria criteria) {
        Specification<Purchase> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Purchase_.id),
                buildStringSpecification(criteria.getProductName(), Purchase_.productName),
                buildRangeSpecification(criteria.getQuantity(), Purchase_.quantity),
                buildRangeSpecification(criteria.getUnitPrice(), Purchase_.unitPrice),
                buildRangeSpecification(criteria.getLineTotal(), Purchase_.lineTotal),
                buildSpecification(criteria.getActive(), Purchase_.active),
                buildStringSpecification(criteria.getCreatedBy(), Purchase_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), Purchase_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), Purchase_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), Purchase_.lastModifiedDate),
                buildSpecification(criteria.getPurchaseOperationId(), root ->
                    root.join(Purchase_.purchaseOperation, JoinType.LEFT).get(PurchaseOperation_.id)
                ),
                buildSpecification(criteria.getProductId(), root -> root.join(Purchase_.product, JoinType.LEFT).get(Product_.id)),
                buildSpecification(criteria.getSupplierId(), root -> root.join(Purchase_.supplier, JoinType.LEFT).get(Supplier_.id)),
                buildSpecification(criteria.getPurchasePaymentId(), root ->
                    root.join(Purchase_.purchasePayments, JoinType.LEFT).get(PurchasePayment_.id)
                )
            );
        }
        return specification;
    }
}
