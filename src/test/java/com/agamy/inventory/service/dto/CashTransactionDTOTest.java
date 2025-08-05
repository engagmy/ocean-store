package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CashTransactionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashTransactionDTO.class);
        CashTransactionDTO cashTransactionDTO1 = new CashTransactionDTO();
        cashTransactionDTO1.setId(1L);
        CashTransactionDTO cashTransactionDTO2 = new CashTransactionDTO();
        assertThat(cashTransactionDTO1).isNotEqualTo(cashTransactionDTO2);
        cashTransactionDTO2.setId(cashTransactionDTO1.getId());
        assertThat(cashTransactionDTO1).isEqualTo(cashTransactionDTO2);
        cashTransactionDTO2.setId(2L);
        assertThat(cashTransactionDTO1).isNotEqualTo(cashTransactionDTO2);
        cashTransactionDTO1.setId(null);
        assertThat(cashTransactionDTO1).isNotEqualTo(cashTransactionDTO2);
    }
}
