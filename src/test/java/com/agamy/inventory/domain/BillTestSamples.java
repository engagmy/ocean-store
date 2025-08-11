package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BillTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Bill getBillSample1() {
        return new Bill().id(1L).billNumber("billNumber1").notes("notes1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static Bill getBillSample2() {
        return new Bill().id(2L).billNumber("billNumber2").notes("notes2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static Bill getBillRandomSampleGenerator() {
        return new Bill()
            .id(longCount.incrementAndGet())
            .billNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
