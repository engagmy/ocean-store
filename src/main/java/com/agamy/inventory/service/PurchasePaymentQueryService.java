package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.PurchasePayment;
import com.agamy.inventory.repository.PurchasePaymentRepository;
import com.agamy.inventory.service.criteria.PurchasePaymentCriteria;
import com.agamy.inventory.service.dto.PurchasePaymentDTO;
import com.agamy.inventory.service.mapper.PurchasePaymentMapper;
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
 * Service for executing complex queries for {@link PurchasePayment} entities in the database.
 * The main input is a {@link PurchasePaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PurchasePaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PurchasePaymentQueryService extends QueryService<PurchasePayment> {

    private static final Logger LOG = LoggerFactory.getLogger(PurchasePaymentQueryService.class);

    private final PurchasePaymentRepository purchasePaymentRepository;

    private final PurchasePaymentMapper purchasePaymentMapper;

    public PurchasePaymentQueryService(PurchasePaymentRepository purchasePaymentRepository, PurchasePaymentMapper purchasePaymentMapper) {
        this.purchasePaymentRepository = purchasePaymentRepository;
        this.purchasePaymentMapper = purchasePaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link PurchasePaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchasePaymentDTO> findByCriteria(PurchasePaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PurchasePayment> specification = createSpecification(criteria);
        return purchasePaymentRepository.findAll(specification, page).map(purchasePaymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PurchasePaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PurchasePayment> specification = createSpecification(criteria);
        return purchasePaymentRepository.count(specification);
    }

    /**
     * Function to convert {@link PurchasePaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PurchasePayment> createSpecification(PurchasePaymentCriteria criteria) {
        Specification<PurchasePayment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), PurchasePayment_.id),
                buildRangeSpecification(criteria.getDate(), PurchasePayment_.date),
                buildRangeSpecification(criteria.getAmount(), PurchasePayment_.amount),
                buildSpecification(criteria.getActive(), PurchasePayment_.active),
                buildStringSpecification(criteria.getCreatedBy(), PurchasePayment_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), PurchasePayment_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), PurchasePayment_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), PurchasePayment_.lastModifiedDate),
                buildSpecification(criteria.getPurchaseId(), root -> root.join(PurchasePayment_.purchase, JoinType.LEFT).get(Purchase_.id))
            );
        }
        return specification;
    }
}
