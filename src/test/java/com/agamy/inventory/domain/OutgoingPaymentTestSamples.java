package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OutgoingPaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OutgoingPayment getOutgoingPaymentSample1() {
        return new OutgoingPayment().id(1L).reason("reason1").notes("notes1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static OutgoingPayment getOutgoingPaymentSample2() {
        return new OutgoingPayment().id(2L).reason("reason2").notes("notes2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static OutgoingPayment getOutgoingPaymentRandomSampleGenerator() {
        return new OutgoingPayment()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
