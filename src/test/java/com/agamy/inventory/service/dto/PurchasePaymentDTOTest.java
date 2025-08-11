package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PurchasePaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PurchasePaymentDTO.class);
        PurchasePaymentDTO purchasePaymentDTO1 = new PurchasePaymentDTO();
        purchasePaymentDTO1.setId(1L);
        PurchasePaymentDTO purchasePaymentDTO2 = new PurchasePaymentDTO();
        assertThat(purchasePaymentDTO1).isNotEqualTo(purchasePaymentDTO2);
        purchasePaymentDTO2.setId(purchasePaymentDTO1.getId());
        assertThat(purchasePaymentDTO1).isEqualTo(purchasePaymentDTO2);
        purchasePaymentDTO2.setId(2L);
        assertThat(purchasePaymentDTO1).isNotEqualTo(purchasePaymentDTO2);
        purchasePaymentDTO1.setId(null);
        assertThat(purchasePaymentDTO1).isNotEqualTo(purchasePaymentDTO2);
    }
}
