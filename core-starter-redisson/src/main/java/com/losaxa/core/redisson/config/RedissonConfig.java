package com.losaxa.core.redisson.config;

import com.losaxa.core.common.JsonUtil;
import com.losaxa.core.redisson.RedissonCahce;
import com.losaxa.core.redisson.RedissonDistributedLock;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({RedissonCahce.class, RedissonDistributedLock.class})
@Configuration
public class RedissonConfig implements RedissonAutoConfigurationCustomizer {

    @Override
    public void customize(Config configuration) {
        JsonJacksonCodec codec = new JsonJacksonCodec(JsonUtil.OM);
        configuration.setCodec(codec);
    }

}
