package com.losaxa.core.common.cache;

import java.util.concurrent.TimeUnit;

public interface RBucket<V> {
    V get();
    void set(V value);
    void set(V value, long time, TimeUnit timeUnit);
}
