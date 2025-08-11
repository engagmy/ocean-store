package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DailyCashDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DailyCashDetail getDailyCashDetailSample1() {
        return new DailyCashDetail()
            .id(1L)
            .referenceId(1L)
            .referenceType("referenceType1")
            .description("description1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static DailyCashDetail getDailyCashDetailSample2() {
        return new DailyCashDetail()
            .id(2L)
            .referenceId(2L)
            .referenceType("referenceType2")
            .description("description2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static DailyCashDetail getDailyCashDetailRandomSampleGenerator() {
        return new DailyCashDetail()
            .id(longCount.incrementAndGet())
            .referenceId(longCount.incrementAndGet())
            .referenceType(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
