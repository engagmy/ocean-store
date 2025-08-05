package com.agamy.inventory.repository;

import com.agamy.inventory.domain.PurchasePayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PurchasePayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchasePaymentRepository extends JpaRepository<PurchasePayment, Long>, JpaSpecificationExecutor<PurchasePayment> {}
