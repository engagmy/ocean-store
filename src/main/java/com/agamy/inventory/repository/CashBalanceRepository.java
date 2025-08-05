package com.agamy.inventory.repository;

import com.agamy.inventory.domain.CashBalance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CashBalance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CashBalanceRepository extends JpaRepository<CashBalance, Long>, JpaSpecificationExecutor<CashBalance> {}
