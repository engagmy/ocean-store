package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DailyCashReconciliationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DailyCashReconciliationDTO.class);
        DailyCashReconciliationDTO dailyCashReconciliationDTO1 = new DailyCashReconciliationDTO();
        dailyCashReconciliationDTO1.setId(1L);
        DailyCashReconciliationDTO dailyCashReconciliationDTO2 = new DailyCashReconciliationDTO();
        assertThat(dailyCashReconciliationDTO1).isNotEqualTo(dailyCashReconciliationDTO2);
        dailyCashReconciliationDTO2.setId(dailyCashReconciliationDTO1.getId());
        assertThat(dailyCashReconciliationDTO1).isEqualTo(dailyCashReconciliationDTO2);
        dailyCashReconciliationDTO2.setId(2L);
        assertThat(dailyCashReconciliationDTO1).isNotEqualTo(dailyCashReconciliationDTO2);
        dailyCashReconciliationDTO1.setId(null);
        assertThat(dailyCashReconciliationDTO1).isNotEqualTo(dailyCashReconciliationDTO2);
    }
}
