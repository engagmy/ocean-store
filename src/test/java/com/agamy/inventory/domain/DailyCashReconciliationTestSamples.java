package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DailyCashReconciliationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DailyCashReconciliation getDailyCashReconciliationSample1() {
        return new DailyCashReconciliation().id(1L).notes("notes1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static DailyCashReconciliation getDailyCashReconciliationSample2() {
        return new DailyCashReconciliation().id(2L).notes("notes2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static DailyCashReconciliation getDailyCashReconciliationRandomSampleGenerator() {
        return new DailyCashReconciliation()
            .id(longCount.incrementAndGet())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
