package com.agamy.inventory.repository;

import com.agamy.inventory.domain.SaleOperation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleOperation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleOperationRepository extends JpaRepository<SaleOperation, Long>, JpaSpecificationExecutor<SaleOperation> {}
