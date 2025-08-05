package com.agamy.inventory.repository;

import com.agamy.inventory.domain.DailyCashDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DailyCashDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DailyCashDetailRepository extends JpaRepository<DailyCashDetail, Long>, JpaSpecificationExecutor<DailyCashDetail> {}
