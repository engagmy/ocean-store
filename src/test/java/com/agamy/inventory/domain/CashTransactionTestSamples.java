package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CashTransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CashTransaction getCashTransactionSample1() {
        return new CashTransaction().id(1L).reason("reason1").createdBy("createdBy1").lastModifiedBy("lastModifiedBy1");
    }

    public static CashTransaction getCashTransactionSample2() {
        return new CashTransaction().id(2L).reason("reason2").createdBy("createdBy2").lastModifiedBy("lastModifiedBy2");
    }

    public static CashTransaction getCashTransactionRandomSampleGenerator() {
        return new CashTransaction()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
