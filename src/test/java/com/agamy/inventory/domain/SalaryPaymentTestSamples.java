package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SalaryPaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SalaryPayment getSalaryPaymentSample1() {
        return new SalaryPayment().id(1L).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static SalaryPayment getSalaryPaymentSample2() {
        return new SalaryPayment().id(2L).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static SalaryPayment getSalaryPaymentRandomSampleGenerator() {
        return new SalaryPayment()
            .id(longCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
