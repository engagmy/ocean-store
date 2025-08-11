package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalePaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalePaymentDTO.class);
        SalePaymentDTO salePaymentDTO1 = new SalePaymentDTO();
        salePaymentDTO1.setId(1L);
        SalePaymentDTO salePaymentDTO2 = new SalePaymentDTO();
        assertThat(salePaymentDTO1).isNotEqualTo(salePaymentDTO2);
        salePaymentDTO2.setId(salePaymentDTO1.getId());
        assertThat(salePaymentDTO1).isEqualTo(salePaymentDTO2);
        salePaymentDTO2.setId(2L);
        assertThat(salePaymentDTO1).isNotEqualTo(salePaymentDTO2);
        salePaymentDTO1.setId(null);
        assertThat(salePaymentDTO1).isNotEqualTo(salePaymentDTO2);
    }
}
