package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.PurchaseOperation;
import com.agamy.inventory.repository.PurchaseOperationRepository;
import com.agamy.inventory.service.criteria.PurchaseOperationCriteria;
import com.agamy.inventory.service.dto.PurchaseOperationDTO;
import com.agamy.inventory.service.mapper.PurchaseOperationMapper;
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
 * Service for executing complex queries for {@link PurchaseOperation} entities in the database.
 * The main input is a {@link PurchaseOperationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PurchaseOperationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PurchaseOperationQueryService extends QueryService<PurchaseOperation> {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOperationQueryService.class);

    private final PurchaseOperationRepository purchaseOperationRepository;

    private final PurchaseOperationMapper purchaseOperationMapper;

    public PurchaseOperationQueryService(
        PurchaseOperationRepository purchaseOperationRepository,
        PurchaseOperationMapper purchaseOperationMapper
    ) {
        this.purchaseOperationRepository = purchaseOperationRepository;
        this.purchaseOperationMapper = purchaseOperationMapper;
    }

    /**
     * Return a {@link Page} of {@link PurchaseOperationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseOperationDTO> findByCriteria(PurchaseOperationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PurchaseOperation> specification = createSpecification(criteria);
        return purchaseOperationRepository.findAll(specification, page).map(purchaseOperationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PurchaseOperationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PurchaseOperation> specification = createSpecification(criteria);
        return purchaseOperationRepository.count(specification);
    }

    /**
     * Function to convert {@link PurchaseOperationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PurchaseOperation> createSpecification(PurchaseOperationCriteria criteria) {
        Specification<PurchaseOperation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PurchaseOperation_.id),
                buildRangeSpecification(criteria.getDate(), PurchaseOperation_.date),
                buildStringSpecification(criteria.getSupplierInvoiceNo(), PurchaseOperation_.supplierInvoiceNo),
                buildRangeSpecification(criteria.getTotalQuantity(), PurchaseOperation_.totalQuantity),
                buildRangeSpecification(criteria.getTotalAmount(), PurchaseOperation_.totalAmount),
                buildRangeSpecification(criteria.getGrandTotal(), PurchaseOperation_.grandTotal),
                buildSpecification(criteria.getActive(), PurchaseOperation_.active),
                buildStringSpecification(criteria.getCreatedBy(), PurchaseOperation_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), PurchaseOperation_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), PurchaseOperation_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), PurchaseOperation_.lastModifiedDate),
                buildSpecification(criteria.getBillId(), root -> root.join(PurchaseOperation_.bill, JoinType.LEFT).get(Bill_.id)),
                buildSpecification(criteria.getPurchaseId(), root ->
                    root.join(PurchaseOperation_.purchases, JoinType.LEFT).get(Purchase_.id)
                )
            );
        }
        return specification;
    }
}
