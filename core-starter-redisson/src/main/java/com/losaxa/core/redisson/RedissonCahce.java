package com.losaxa.core.redisson;

import com.losaxa.core.common.cache.Cache;
import com.losaxa.core.common.cache.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedissonCahce implements Cache {

    @Autowired
    RedissonClient redissonClient;

    @Override
    public <V> RBucket<V> getBucket(String key) {
        return new RedissonRBucket<>(redissonClient.getBucket(key));
    }

}
