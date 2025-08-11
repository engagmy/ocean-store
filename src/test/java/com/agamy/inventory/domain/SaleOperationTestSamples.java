package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SaleOperationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SaleOperation getSaleOperationSample1() {
        return new SaleOperation().id(1L).totalQuantity(1).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static SaleOperation getSaleOperationSample2() {
        return new SaleOperation().id(2L).totalQuantity(2).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static SaleOperation getSaleOperationRandomSampleGenerator() {
        return new SaleOperation()
            .id(longCount.incrementAndGet())
            .totalQuantity(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
