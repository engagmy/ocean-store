package com.agamy.inventory.repository;

import com.agamy.inventory.domain.InventoryTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryTransactionRepository
    extends JpaRepository<InventoryTransaction, Long>, JpaSpecificationExecutor<InventoryTransaction> {}
