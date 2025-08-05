package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleOperationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleOperationDTO.class);
        SaleOperationDTO saleOperationDTO1 = new SaleOperationDTO();
        saleOperationDTO1.setId(1L);
        SaleOperationDTO saleOperationDTO2 = new SaleOperationDTO();
        assertThat(saleOperationDTO1).isNotEqualTo(saleOperationDTO2);
        saleOperationDTO2.setId(saleOperationDTO1.getId());
        assertThat(saleOperationDTO1).isEqualTo(saleOperationDTO2);
        saleOperationDTO2.setId(2L);
        assertThat(saleOperationDTO1).isNotEqualTo(saleOperationDTO2);
        saleOperationDTO1.setId(null);
        assertThat(saleOperationDTO1).isNotEqualTo(saleOperationDTO2);
    }
}
