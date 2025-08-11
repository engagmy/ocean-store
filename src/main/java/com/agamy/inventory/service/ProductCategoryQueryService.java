package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.ProductCategory;
import com.agamy.inventory.repository.ProductCategoryRepository;
import com.agamy.inventory.service.criteria.ProductCategoryCriteria;
import com.agamy.inventory.service.dto.ProductCategoryDTO;
import com.agamy.inventory.service.mapper.ProductCategoryMapper;
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
 * Service for executing complex queries for {@link ProductCategory} entities in the database.
 * The main input is a {@link ProductCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProductCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductCategoryQueryService extends QueryService<ProductCategory> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryQueryService.class);

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryQueryService(ProductCategoryRepository productCategoryRepository, ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    /**
     * Return a {@link Page} of {@link ProductCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductCategoryDTO> findByCriteria(ProductCategoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryRepository.findAll(specification, page).map(productCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCategoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProductCategory> specification = createSpecification(criteria);
        return productCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductCategory> createSpecification(ProductCategoryCriteria criteria) {
        Specification<ProductCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), ProductCategory_.id),
                buildStringSpecification(criteria.getName(), ProductCategory_.name),
                buildSpecification(criteria.getActive(), ProductCategory_.active),
                buildStringSpecification(criteria.getCreatedBy(), ProductCategory_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), ProductCategory_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), ProductCategory_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), ProductCategory_.lastModifiedDate),
                buildSpecification(criteria.getProductId(), root -> root.join(ProductCategory_.products, JoinType.LEFT).get(Product_.id))
            );
        }
        return specification;
    }
}
