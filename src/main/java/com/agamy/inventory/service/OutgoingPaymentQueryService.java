package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.OutgoingPayment;
import com.agamy.inventory.repository.OutgoingPaymentRepository;
import com.agamy.inventory.service.criteria.OutgoingPaymentCriteria;
import com.agamy.inventory.service.dto.OutgoingPaymentDTO;
import com.agamy.inventory.service.mapper.OutgoingPaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OutgoingPayment} entities in the database.
 * The main input is a {@link OutgoingPaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OutgoingPaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OutgoingPaymentQueryService extends QueryService<OutgoingPayment> {

    private static final Logger LOG = LoggerFactory.getLogger(OutgoingPaymentQueryService.class);

    private final OutgoingPaymentRepository outgoingPaymentRepository;

    private final OutgoingPaymentMapper outgoingPaymentMapper;

    public OutgoingPaymentQueryService(OutgoingPaymentRepository outgoingPaymentRepository, OutgoingPaymentMapper outgoingPaymentMapper) {
        this.outgoingPaymentRepository = outgoingPaymentRepository;
        this.outgoingPaymentMapper = outgoingPaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link OutgoingPaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OutgoingPaymentDTO> findByCriteria(OutgoingPaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OutgoingPayment> specification = createSpecification(criteria);
        return outgoingPaymentRepository.findAll(specification, page).map(outgoingPaymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OutgoingPaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OutgoingPayment> specification = createSpecification(criteria);
        return outgoingPaymentRepository.count(specification);
    }

    /**
     * Function to convert {@link OutgoingPaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OutgoingPayment> createSpecification(OutgoingPaymentCriteria criteria) {
        Specification<OutgoingPayment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), OutgoingPayment_.id),
                buildRangeSpecification(criteria.getDate(), OutgoingPayment_.date),
                buildRangeSpecification(criteria.getAmount(), OutgoingPayment_.amount),
                buildStringSpecification(criteria.getReason(), OutgoingPayment_.reason),
                buildStringSpecification(criteria.getNotes(), OutgoingPayment_.notes),
                buildSpecification(criteria.getActive(), OutgoingPayment_.active),
                buildStringSpecification(criteria.getCreatedBy(), OutgoingPayment_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), OutgoingPayment_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), OutgoingPayment_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), OutgoingPayment_.lastModifiedDate)
            );
        }
        return specification;
    }
}
