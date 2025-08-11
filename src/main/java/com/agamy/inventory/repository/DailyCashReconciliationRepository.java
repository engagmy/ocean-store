package com.agamy.inventory.repository;

import com.agamy.inventory.domain.DailyCashReconciliation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DailyCashReconciliation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DailyCashReconciliationRepository
    extends JpaRepository<DailyCashReconciliation, Long>, JpaSpecificationExecutor<DailyCashReconciliation> {}
