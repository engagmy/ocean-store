package com.agamy.inventory.repository;

import com.agamy.inventory.domain.PurchaseOperation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PurchaseOperation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseOperationRepository extends JpaRepository<PurchaseOperation, Long>, JpaSpecificationExecutor<PurchaseOperation> {}
