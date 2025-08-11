package com.agamy.inventory.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PurchaseOperationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PurchaseOperation getPurchaseOperationSample1() {
        return new PurchaseOperation()
            .id(1L)
            .supplierInvoiceNo("supplierInvoiceNo1")
            .totalQuantity(1)
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1");
    }

    public static PurchaseOperation getPurchaseOperationSample2() {
        return new PurchaseOperation()
            .id(2L)
            .supplierInvoiceNo("supplierInvoiceNo2")
            .totalQuantity(2)
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2");
    }

    public static PurchaseOperation getPurchaseOperationRandomSampleGenerator() {
        return new PurchaseOperation()
            .id(longCount.incrementAndGet())
            .supplierInvoiceNo(UUID.randomUUID().toString())
            .totalQuantity(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString());
    }
}
