package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.SaleOperation;
import com.agamy.inventory.repository.SaleOperationRepository;
import com.agamy.inventory.service.criteria.SaleOperationCriteria;
import com.agamy.inventory.service.dto.SaleOperationDTO;
import com.agamy.inventory.service.mapper.SaleOperationMapper;
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
 * Service for executing complex queries for {@link SaleOperation} entities in the database.
 * The main input is a {@link SaleOperationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SaleOperationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SaleOperationQueryService extends QueryService<SaleOperation> {

    private static final Logger LOG = LoggerFactory.getLogger(SaleOperationQueryService.class);

    private final SaleOperationRepository saleOperationRepository;

    private final SaleOperationMapper saleOperationMapper;

    public SaleOperationQueryService(SaleOperationRepository saleOperationRepository, SaleOperationMapper saleOperationMapper) {
        this.saleOperationRepository = saleOperationRepository;
        this.saleOperationMapper = saleOperationMapper;
    }

    /**
     * Return a {@link Page} of {@link SaleOperationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleOperationDTO> findByCriteria(SaleOperationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SaleOperation> specification = createSpecification(criteria);
        return saleOperationRepository.findAll(specification, page).map(saleOperationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SaleOperationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SaleOperation> specification = createSpecification(criteria);
        return saleOperationRepository.count(specification);
    }

    /**
     * Function to convert {@link SaleOperationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SaleOperation> createSpecification(SaleOperationCriteria criteria) {
        Specification<SaleOperation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SaleOperation_.id),
                buildRangeSpecification(criteria.getDate(), SaleOperation_.date),
                buildRangeSpecification(criteria.getTotalQuantity(), SaleOperation_.totalQuantity),
                buildRangeSpecification(criteria.getTotalAmount(), SaleOperation_.totalAmount),
                buildRangeSpecification(criteria.getTotalDiscount(), SaleOperation_.totalDiscount),
                buildRangeSpecification(criteria.getGrandTotal(), SaleOperation_.grandTotal),
                buildSpecification(criteria.getActive(), SaleOperation_.active),
                buildStringSpecification(criteria.getCreatedBy(), SaleOperation_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), SaleOperation_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), SaleOperation_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), SaleOperation_.lastModifiedDate),
                buildSpecification(criteria.getBillId(), root -> root.join(SaleOperation_.bill, JoinType.LEFT).get(Bill_.id)),
                buildSpecification(criteria.getCustomerId(), root -> root.join(SaleOperation_.customer, JoinType.LEFT).get(Customer_.id)),
                buildSpecification(criteria.getSaleId(), root -> root.join(SaleOperation_.sales, JoinType.LEFT).get(Sale_.id))
            );
        }
        return specification;
    }
}
