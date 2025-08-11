package com.agamy.inventory.repository;

import com.agamy.inventory.domain.OutgoingPayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OutgoingPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OutgoingPaymentRepository extends JpaRepository<OutgoingPayment, Long>, JpaSpecificationExecutor<OutgoingPayment> {}
