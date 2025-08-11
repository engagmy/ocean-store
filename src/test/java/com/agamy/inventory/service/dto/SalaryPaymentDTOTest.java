package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalaryPaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalaryPaymentDTO.class);
        SalaryPaymentDTO salaryPaymentDTO1 = new SalaryPaymentDTO();
        salaryPaymentDTO1.setId(1L);
        SalaryPaymentDTO salaryPaymentDTO2 = new SalaryPaymentDTO();
        assertThat(salaryPaymentDTO1).isNotEqualTo(salaryPaymentDTO2);
        salaryPaymentDTO2.setId(salaryPaymentDTO1.getId());
        assertThat(salaryPaymentDTO1).isEqualTo(salaryPaymentDTO2);
        salaryPaymentDTO2.setId(2L);
        assertThat(salaryPaymentDTO1).isNotEqualTo(salaryPaymentDTO2);
        salaryPaymentDTO1.setId(null);
        assertThat(salaryPaymentDTO1).isNotEqualTo(salaryPaymentDTO2);
    }
}
