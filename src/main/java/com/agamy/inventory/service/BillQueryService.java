package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.Bill;
import com.agamy.inventory.repository.BillRepository;
import com.agamy.inventory.service.criteria.BillCriteria;
import com.agamy.inventory.service.dto.BillDTO;
import com.agamy.inventory.service.mapper.BillMapper;
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
 * Service for executing complex queries for {@link Bill} entities in the database.
 * The main input is a {@link BillCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BillDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BillQueryService extends QueryService<Bill> {

    private static final Logger LOG = LoggerFactory.getLogger(BillQueryService.class);

    private final BillRepository billRepository;

    private final BillMapper billMapper;

    public BillQueryService(BillRepository billRepository, BillMapper billMapper) {
        this.billRepository = billRepository;
        this.billMapper = billMapper;
    }

    /**
     * Return a {@link Page} of {@link BillDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BillDTO> findByCriteria(BillCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Bill> specification = createSpecification(criteria);
        return billRepository.findAll(specification, page).map(billMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BillCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Bill> specification = createSpecification(criteria);
        return billRepository.count(specification);
    }

    /**
     * Function to convert {@link BillCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Bill> createSpecification(BillCriteria criteria) {
        Specification<Bill> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Bill_.id),
                buildStringSpecification(criteria.getBillNumber(), Bill_.billNumber),
                buildRangeSpecification(criteria.getDate(), Bill_.date),
                buildRangeSpecification(criteria.getTotalAmount(), Bill_.totalAmount),
                buildRangeSpecification(criteria.getTaxAmount(), Bill_.taxAmount),
                buildRangeSpecification(criteria.getDiscountAmount(), Bill_.discountAmount),
                buildRangeSpecification(criteria.getPaidAmount(), Bill_.paidAmount),
                buildRangeSpecification(criteria.getDueAmount(), Bill_.dueAmount),
                buildStringSpecification(criteria.getNotes(), Bill_.notes),
                buildSpecification(criteria.getActive(), Bill_.active),
                buildStringSpecification(criteria.getCreatedBy(), Bill_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), Bill_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), Bill_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), Bill_.lastModifiedDate),
                buildSpecification(criteria.getSaleOperationId(), root ->
                    root.join(Bill_.saleOperations, JoinType.LEFT).get(SaleOperation_.id)
                ),
                buildSpecification(criteria.getPurchaseOperationId(), root ->
                    root.join(Bill_.purchaseOperations, JoinType.LEFT).get(PurchaseOperation_.id)
                )
            );
        }
        return specification;
    }
}
