package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.SalePayment;
import com.agamy.inventory.repository.SalePaymentRepository;
import com.agamy.inventory.service.criteria.SalePaymentCriteria;
import com.agamy.inventory.service.dto.SalePaymentDTO;
import com.agamy.inventory.service.mapper.SalePaymentMapper;
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
 * Service for executing complex queries for {@link SalePayment} entities in the database.
 * The main input is a {@link SalePaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SalePaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalePaymentQueryService extends QueryService<SalePayment> {

    private static final Logger LOG = LoggerFactory.getLogger(SalePaymentQueryService.class);

    private final SalePaymentRepository salePaymentRepository;

    private final SalePaymentMapper salePaymentMapper;

    public SalePaymentQueryService(SalePaymentRepository salePaymentRepository, SalePaymentMapper salePaymentMapper) {
        this.salePaymentRepository = salePaymentRepository;
        this.salePaymentMapper = salePaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link SalePaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalePaymentDTO> findByCriteria(SalePaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalePayment> specification = createSpecification(criteria);
        return salePaymentRepository.findAll(specification, page).map(salePaymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalePaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SalePayment> specification = createSpecification(criteria);
        return salePaymentRepository.count(specification);
    }

    /**
     * Function to convert {@link SalePaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalePayment> createSpecification(SalePaymentCriteria criteria) {
        Specification<SalePayment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SalePayment_.id),
                buildRangeSpecification(criteria.getDate(), SalePayment_.date),
                buildRangeSpecification(criteria.getAmount(), SalePayment_.amount),
                buildSpecification(criteria.getActive(), SalePayment_.active),
                buildStringSpecification(criteria.getCreatedBy(), SalePayment_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), SalePayment_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), SalePayment_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), SalePayment_.lastModifiedDate),
                buildSpecification(criteria.getSaleId(), root -> root.join(SalePayment_.sale, JoinType.LEFT).get(Sale_.id))
            );
        }
        return specification;
    }
}
