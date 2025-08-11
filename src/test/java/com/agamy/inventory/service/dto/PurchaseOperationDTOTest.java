package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchaseOperationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchaseOperationDTO.class);
        PurchaseOperationDTO purchaseOperationDTO1 = new PurchaseOperationDTO();
        purchaseOperationDTO1.setId(1L);
        PurchaseOperationDTO purchaseOperationDTO2 = new PurchaseOperationDTO();
        assertThat(purchaseOperationDTO1).isNotEqualTo(purchaseOperationDTO2);
        purchaseOperationDTO2.setId(purchaseOperationDTO1.getId());
        assertThat(purchaseOperationDTO1).isEqualTo(purchaseOperationDTO2);
        purchaseOperationDTO2.setId(2L);
        assertThat(purchaseOperationDTO1).isNotEqualTo(purchaseOperationDTO2);
        purchaseOperationDTO1.setId(null);
        assertThat(purchaseOperationDTO1).isNotEqualTo(purchaseOperationDTO2);
    }
}
