package com.losaxa.core.common.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface DistributedLock {

    <T> T tryLock(String key,
                  long waitTime,
                  long leaseTime,
                  TimeUnit unit,
                  Supplier<T> supplier);

    void tryLock(String key,
                 long waitTime,
                 long leaseTime,
                 TimeUnit unit,
                 Runnable runnable);

}
