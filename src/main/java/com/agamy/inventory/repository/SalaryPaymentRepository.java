package com.agamy.inventory.repository;

import com.agamy.inventory.domain.SalaryPayment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SalaryPayment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SalaryPaymentRepository extends JpaRepository<SalaryPayment, Long>, JpaSpecificationExecutor<SalaryPayment> {}
