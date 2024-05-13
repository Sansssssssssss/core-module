package com.losaxa.core.redisson;

import com.losaxa.core.common.cache.RBucket;

import java.util.concurrent.TimeUnit;

public class RedissonRBucket<V> implements RBucket<V> {

    private final org.redisson.api.RBucket<V> rBucket;

    public RedissonRBucket(org.redisson.api.RBucket<V> rBucket) {
        this.rBucket = rBucket;
    }

    @Override
    public V get() {
        return rBucket.get();
    }

    @Override
    public void set(V value) {
        rBucket.set(value);
    }

    @Override
    public void set(V va,
                    long time,
                    TimeUnit timeUnit) {
        rBucket.set(va, time, timeUnit);
    }
}
