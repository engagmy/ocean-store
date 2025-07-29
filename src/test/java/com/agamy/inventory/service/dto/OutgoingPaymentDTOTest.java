package com.agamy.inventory.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OutgoingPaymentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OutgoingPaymentDTO.class);
        OutgoingPaymentDTO outgoingPaymentDTO1 = new OutgoingPaymentDTO();
        outgoingPaymentDTO1.setId(1L);
        OutgoingPaymentDTO outgoingPaymentDTO2 = new OutgoingPaymentDTO();
        assertThat(outgoingPaymentDTO1).isNotEqualTo(outgoingPaymentDTO2);
        outgoingPaymentDTO2.setId(outgoingPaymentDTO1.getId());
        assertThat(outgoingPaymentDTO1).isEqualTo(outgoingPaymentDTO2);
        outgoingPaymentDTO2.setId(2L);
        assertThat(outgoingPaymentDTO1).isNotEqualTo(outgoingPaymentDTO2);
        outgoingPaymentDTO1.setId(null);
        assertThat(outgoingPaymentDTO1).isNotEqualTo(outgoingPaymentDTO2);
    }
}
