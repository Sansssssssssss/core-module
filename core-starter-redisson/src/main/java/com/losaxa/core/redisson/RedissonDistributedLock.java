package com.losaxa.core.redisson;

import com.losaxa.core.common.lock.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedissonDistributedLock implements DistributedLock {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedissonClient redissonClient;


    @Override
    public <T> T tryLock(String key,
                         long waitTime,
                         long leaseTime,
                         TimeUnit unit,
                         Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        tryLock(key, waitTime, leaseTime, unit, lock);
        try {
            return supplier.get();
        } finally {
            unlock(lock, key);
        }
    }

    @Override
    public void tryLock(String key,
                        long waitTime,
                        long leaseTime,
                        TimeUnit unit,
                        Runnable runnable) {
        RLock lock = redissonClient.getLock(key);
        tryLock(key, waitTime, leaseTime, unit, lock);
        try {
            runnable.run();
        } finally {
            unlock(lock, key);
        }
    }

    private void tryLock(String key,
                         long waitTime,
                         long leaseTime,
                         TimeUnit unit,
                         RLock lock) {
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(String.format("lock failed: %s; msg: %s", key, e.getMessage()), e);
        }
        if (!isLocked) {
            throw new RuntimeException("lock failed: " + key);
        }
        log.info("lock success: {}", key);
    }

    private void unlock(RLock lock,
                        String key) {
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            log.info("unlock success: {}", key);
            lock.unlock();
        }
    }
}
