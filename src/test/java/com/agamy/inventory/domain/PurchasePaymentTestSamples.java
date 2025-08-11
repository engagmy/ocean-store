package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PurchasePaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PurchasePayment getPurchasePaymentSample1() {
        return new PurchasePayment().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static PurchasePayment getPurchasePaymentSample2() {
        return new PurchasePayment().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static PurchasePayment getPurchasePaymentRandomSampleGenerator() {
        return new PurchasePayment()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
