package com.agamy.inventory.domain;

import static com.agamy.inventory.domain.OutgoingPaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.agamy.inventory.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OutgoingPaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OutgoingPayment.class);
        OutgoingPayment outgoingPayment1 = getOutgoingPaymentSample1();
        OutgoingPayment outgoingPayment2 = new OutgoingPayment();
        assertThat(outgoingPayment1).isNotEqualTo(outgoingPayment2);

        outgoingPayment2.setId(outgoingPayment1.getId());
        assertThat(outgoingPayment1).isEqualTo(outgoingPayment2);

        outgoingPayment2 = getOutgoingPaymentSample2();
        assertThat(outgoingPayment1).isNotEqualTo(outgoingPayment2);
    }
}
