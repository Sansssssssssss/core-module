package com.losaxa.core.common.cache;

public interface Cache {

    <V> RBucket<V> getBucket(String key);

}
