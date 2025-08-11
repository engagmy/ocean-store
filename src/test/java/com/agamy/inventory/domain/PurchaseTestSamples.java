package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Purchase getPurchaseSample1() {
        return new Purchase().id(1L).productName("productName1").quantity(1).createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Purchase getPurchaseSample2() {
        return new Purchase().id(2L).productName("productName2").quantity(2).createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Purchase getPurchaseRandomSampleGenerator() {
        return new Purchase()
            .id(longCount.incrementAndGet())
            .productName(UUID.randomUUID().toString())
            .quantity(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
