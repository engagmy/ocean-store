package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SalePaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SalePayment getSalePaymentSample1() {
        return new SalePayment().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static SalePayment getSalePaymentSample2() {
        return new SalePayment().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static SalePayment getSalePaymentRandomSampleGenerator() {
        return new SalePayment()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
