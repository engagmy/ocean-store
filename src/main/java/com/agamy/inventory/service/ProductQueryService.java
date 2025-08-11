package com.agamy.inventory.service;

import com.agamy.inventory.domain.*; // for static metamodels
import com.agamy.inventory.domain.Product;
import com.agamy.inventory.repository.ProductRepository;
import com.agamy.inventory.service.criteria.ProductCriteria;
import com.agamy.inventory.service.dto.ProductDTO;
import com.agamy.inventory.service.mapper.ProductMapper;
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
 * Service for executing complex queries for {@link Product} entities in the database.
 * The main input is a {@link ProductCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProductDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryService extends QueryService<Product> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductQueryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Return a {@link Page} of {@link ProductDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByCriteria(ProductCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.findAll(specification, page).map(productMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Product> specification = createSpecification(criteria);
        return productRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Product> createSpecification(ProductCriteria criteria) {
        Specification<Product> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Product_.id),
                buildStringSpecification(criteria.getName(), Product_.name),
                buildStringSpecification(criteria.getCode(), Product_.code),
                buildRangeSpecification(criteria.getQuantity(), Product_.quantity),
                buildRangeSpecification(criteria.getUnitPrice(), Product_.unitPrice),
                buildRangeSpecification(criteria.getCostPrice(), Product_.costPrice),
                buildSpecification(criteria.getActive(), Product_.active),
                buildStringSpecification(criteria.getCreatedBy(), Product_.createdBy),
                buildRangeSpecification(criteria.getCreatedDate(), Product_.createdDate),
                buildStringSpecification(criteria.getLastModifiedBy(), Product_.lastModifiedBy),
                buildRangeSpecification(criteria.getLastModifiedDate(), Product_.lastModifiedDate),
                buildSpecification(criteria.getBrandId(), root -> root.join(Product_.brand, JoinType.LEFT).get(Brand_.id)),
                buildSpecification(criteria.getProductCategoryId(), root ->
                    root.join(Product_.productCategory, JoinType.LEFT).get(ProductCategory_.id)
                ),
                buildSpecification(criteria.getInventoryTransactionId(), root ->
                    root.join(Product_.inventoryTransactions, JoinType.LEFT).get(InventoryTransaction_.id)
                ),
                buildSpecification(criteria.getSaleId(), root -> root.join(Product_.sales, JoinType.LEFT).get(Sale_.id)),
                buildSpecification(criteria.getPurchaseId(), root -> root.join(Product_.purchases, JoinType.LEFT).get(Purchase_.id))
            );
        }
        return specification;
    }
}
