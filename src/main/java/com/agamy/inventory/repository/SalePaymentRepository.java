package com.agamy.inventory.repository;

import com.agamy.inventory.domain.SalePayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalePayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalePaymentRepository extends JpaRepository<SalePayment, Long>, JpaSpecificationExecutor<SalePayment> {}
