package com.losaxa.core.mongo.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 多数据源配置
 */
//@AutoConfigureBefore(MongoAutoConfiguration.class)
//@Configuration
public class MongoPropertiesConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.data.mongodb")
    public MongoProperties goodsMongoProperties() {
        return new MongoProperties();
    }

    //todo:luosx:
}
