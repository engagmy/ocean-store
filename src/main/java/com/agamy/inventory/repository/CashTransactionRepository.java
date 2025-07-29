package com.agamy.inventory.repository;

import com.agamy.inventory.domain.CashTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CashTransaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CashTransactionRepository extends JpaRepository<CashTransaction, Long>, JpaSpecificationExecutor<CashTransaction> {}
