package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.DailyCashDetail;
import com.agamy.inventory.repository.DailyCashDetailRepository;
import com.agamy.inventory.service.criteria.DailyCashDetailCriteria;
import com.agamy.inventory.service.dto.DailyCashDetailDTO;
import com.agamy.inventory.service.mapper.DailyCashDetailMapper;
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
 * Service for executing complex queries for {@link DailyCashDetail} entities in the database.
 * The main input is a {@link DailyCashDetailCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DailyCashDetailDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DailyCashDetailQueryService extends QueryService<DailyCashDetail> {

    private static final Logger LOG = LoggerFactory.getLogger(DailyCashDetailQueryService.class);

    private final DailyCashDetailRepository dailyCashDetailRepository;

    private final DailyCashDetailMapper dailyCashDetailMapper;

    public DailyCashDetailQueryService(DailyCashDetailRepository dailyCashDetailRepository, DailyCashDetailMapper dailyCashDetailMapper) {
        this.dailyCashDetailRepository = dailyCashDetailRepository;
        this.dailyCashDetailMapper = dailyCashDetailMapper;
    }

    /**
     * Return a {@link Page} of {@link DailyCashDetailDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DailyCashDetailDTO> findByCriteria(DailyCashDetailCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DailyCashDetail> specification = createSpecification(criteria);
        return dailyCashDetailRepository.findAll(specification, page).map(dailyCashDetailMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DailyCashDetailCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DailyCashDetail> specification = createSpecification(criteria);
        return dailyCashDetailRepository.count(specification);
    }

    /**
     * Function to convert {@link DailyCashDetailCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DailyCashDetail> createSpecification(DailyCashDetailCriteria criteria) {
        Specification<DailyCashDetail> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), DailyCashDetail_.id),
                buildSpecification(criteria.getType(), DailyCashDetail_.type),
                buildRangeSpecification(criteria.getReferenceId(), DailyCashDetail_.referenceId),
                buildStringSpecification(criteria.getReferenceType(), DailyCashDetail_.referenceType),
                buildRangeSpecification(criteria.getAmount(), DailyCashDetail_.amount),
                buildStringSpecification(criteria.getDescription(), DailyCashDetail_.description),
                buildRangeSpecification(criteria.getTimestamp(), DailyCashDetail_.timestamp),
                buildSpecification(criteria.getActive(), DailyCashDetail_.active),
                buildStringSpecification(criteria.getCreatedBy(), DailyCashDetail_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), DailyCashDetail_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), DailyCashDetail_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), DailyCashDetail_.lastModifiedDate),
                buildSpecification(criteria.getDailyCashReconciliationId(), root ->
                    root.join(DailyCashDetail_.dailyCashReconciliation, JoinType.LEFT).get(DailyCashReconciliation_.id)
                )
            );
        }
        return specification;
    }
}
