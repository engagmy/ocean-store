package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.SalaryPayment;
import com.agamy.inventory.repository.SalaryPaymentRepository;
import com.agamy.inventory.service.criteria.SalaryPaymentCriteria;
import com.agamy.inventory.service.dto.SalaryPaymentDTO;
import com.agamy.inventory.service.mapper.SalaryPaymentMapper;
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
 * Service for executing complex queries for {@link SalaryPayment} entities in the database.
 * The main input is a {@link SalaryPaymentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SalaryPaymentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalaryPaymentQueryService extends QueryService<SalaryPayment> {

    private static final Logger LOG = LoggerFactory.getLogger(SalaryPaymentQueryService.class);

    private final SalaryPaymentRepository salaryPaymentRepository;

    private final SalaryPaymentMapper salaryPaymentMapper;

    public SalaryPaymentQueryService(SalaryPaymentRepository salaryPaymentRepository, SalaryPaymentMapper salaryPaymentMapper) {
        this.salaryPaymentRepository = salaryPaymentRepository;
        this.salaryPaymentMapper = salaryPaymentMapper;
    }

    /**
     * Return a {@link Page} of {@link SalaryPaymentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalaryPaymentDTO> findByCriteria(SalaryPaymentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SalaryPayment> specification = createSpecification(criteria);
        return salaryPaymentRepository.findAll(specification, page).map(salaryPaymentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalaryPaymentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SalaryPayment> specification = createSpecification(criteria);
        return salaryPaymentRepository.count(specification);
    }

    /**
     * Function to convert {@link SalaryPaymentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SalaryPayment> createSpecification(SalaryPaymentCriteria criteria) {
        Specification<SalaryPayment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), SalaryPayment_.id),
                buildRangeSpecification(criteria.getDate(), SalaryPayment_.date),
                buildRangeSpecification(criteria.getAmount(), SalaryPayment_.amount),
                buildSpecification(criteria.getActive(), SalaryPayment_.active),
                buildStringSpecification(criteria.getCreatedBy(), SalaryPayment_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), SalaryPayment_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), SalaryPayment_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), SalaryPayment_.lastModifiedDate),
                buildSpecification(criteria.getEmployeeId(), root -> root.join(SalaryPayment_.employee, JoinType.LEFT).get(Employee_.id))
            );
        }
        return specification;
    }
}
