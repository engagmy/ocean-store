package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.CashBalance;
import com.agamy.inventory.repository.CashBalanceRepository;
import com.agamy.inventory.service.criteria.CashBalanceCriteria;
import com.agamy.inventory.service.dto.CashBalanceDTO;
import com.agamy.inventory.service.mapper.CashBalanceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CashBalance} entities in the database.
 * The main input is a {@link CashBalanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CashBalanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CashBalanceQueryService extends QueryService<CashBalance> {

    private static final Logger LOG = LoggerFactory.getLogger(CashBalanceQueryService.class);

    private final CashBalanceRepository cashBalanceRepository;

    private final CashBalanceMapper cashBalanceMapper;

    public CashBalanceQueryService(CashBalanceRepository cashBalanceRepository, CashBalanceMapper cashBalanceMapper) {
        this.cashBalanceRepository = cashBalanceRepository;
        this.cashBalanceMapper = cashBalanceMapper;
    }

    /**
     * Return a {@link Page} of {@link CashBalanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CashBalanceDTO> findByCriteria(CashBalanceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CashBalance> specification = createSpecification(criteria);
        return cashBalanceRepository.findAll(specification, page).map(cashBalanceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CashBalanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CashBalance> specification = createSpecification(criteria);
        return cashBalanceRepository.count(specification);
    }

    /**
     * Function to convert {@link CashBalanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CashBalance> createSpecification(CashBalanceCriteria criteria) {
        Specification<CashBalance> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), CashBalance_.id),
                buildRangeSpecification(criteria.getAvailable(), CashBalance_.available),
                buildRangeSpecification(criteria.getLastUpdated(), CashBalance_.lastUpdated),
                buildStringSpecification(criteria.getNotes(), CashBalance_.notes),
                buildSpecification(criteria.getActive(), CashBalance_.active),
                buildStringSpecification(criteria.getCreatedBy(), CashBalance_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), CashBalance_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), CashBalance_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), CashBalance_.lastModifiedDate)
            );
        }
        return specification;
    }
}
