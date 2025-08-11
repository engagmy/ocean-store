package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.DailyCashReconciliation;
import com.agamy.inventory.repository.DailyCashReconciliationRepository;
import com.agamy.inventory.service.criteria.DailyCashReconciliationCriteria;
import com.agamy.inventory.service.dto.DailyCashReconciliationDTO;
import com.agamy.inventory.service.mapper.DailyCashReconciliationMapper;
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
 * Service for executing complex queries for {@link DailyCashReconciliation} entities in the database.
 * The main input is a {@link DailyCashReconciliationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DailyCashReconciliationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DailyCashReconciliationQueryService extends QueryService<DailyCashReconciliation> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashReconciliationQueryService.class);

    private final DailyCashReconciliationRepository dailyCashReconciliationRepository;

    private final DailyCashReconciliationMapper dailyCashReconciliationMapper;

    public DailyCashReconciliationQueryService(
        DailyCashReconciliationRepository dailyCashReconciliationRepository,
        DailyCashReconciliationMapper dailyCashReconciliationMapper
    ) {
        this.dailyCashReconciliationRepository = dailyCashReconciliationRepository;
        this.dailyCashReconciliationMapper = dailyCashReconciliationMapper;
    }

    /**
     * Return a {@link Page} of {@link DailyCashReconciliationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DailyCashReconciliationDTO> findByCriteria(DailyCashReconciliationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DailyCashReconciliation> specification = createSpecification(criteria);
        return dailyCashReconciliationRepository.findAll(specification, page).map(dailyCashReconciliationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DailyCashReconciliationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DailyCashReconciliation> specification = createSpecification(criteria);
        return dailyCashReconciliationRepository.count(specification);
    }

    /**
     * Function to convert {@link DailyCashReconciliationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DailyCashReconciliation> createSpecification(DailyCashReconciliationCriteria criteria) {
        Specification<DailyCashReconciliation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), DailyCashReconciliation_.id),
                buildRangeSpecification(criteria.getDate(), DailyCashReconciliation_.date),
                buildRangeSpecification(criteria.getOpeningBalance(), DailyCashReconciliation_.openingBalance),
                buildRangeSpecification(criteria.getTotalSales(), DailyCashReconciliation_.totalSales),
                buildRangeSpecification(criteria.getTotalPurchases(), DailyCashReconciliation_.totalPurchases),
                buildRangeSpecification(criteria.getTotalSalaryPaid(), DailyCashReconciliation_.totalSalaryPaid),
                buildRangeSpecification(criteria.getOwnerDeposits(), DailyCashReconciliation_.ownerDeposits),
                buildRangeSpecification(criteria.getWithdrawals(), DailyCashReconciliation_.withdrawals),
                buildRangeSpecification(criteria.getClosingBalance(), DailyCashReconciliation_.closingBalance),
                buildStringSpecification(criteria.getNotes(), DailyCashReconciliation_.notes),
                buildSpecification(criteria.getActive(), DailyCashReconciliation_.active),
                buildStringSpecification(criteria.getCreatedBy(), DailyCashReconciliation_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), DailyCashReconciliation_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), DailyCashReconciliation_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), DailyCashReconciliation_.lastModifiedDate),
                buildSpecification(criteria.getDailyCashDetailId(), root ->
                    root.join(DailyCashReconciliation_.dailyCashDetails, JoinType.LEFT).get(DailyCashDetail_.id)
                )
            );
        }
        return specification;
    }
}
