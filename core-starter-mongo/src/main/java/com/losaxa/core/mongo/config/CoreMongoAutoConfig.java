package com.losaxa.core.mongo.config;

import com.losaxa.core.mongo.UpdateUtil;
import com.losaxa.core.mongo.converter.EnumToIntegerConverter;
import com.losaxa.core.mongo.converter.IntegerToEnumConvertFactory;
import com.losaxa.core.mongo.MongoEventListener;
import com.losaxa.core.mongo.converter.BigDecimalToDecimal128Converter;
import com.losaxa.core.mongo.converter.Decimal128ToBigDecimalConverter;
import com.losaxa.core.mongo.query.QueryUtil;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB相关基础配置
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MongoProperties.class, CoreDbProperties.class})
@ConditionalOnMissingBean(type = {"org.springframework.data.mongodb.MongoDatabaseFactory"})
@AutoConfigureBefore(MongoAutoConfiguration.class)
@Import({UpdateUtil.class, QueryUtil.class, MongoEventListener.class})
public class CoreMongoAutoConfig { //AbstractMongoClientConfiguration, MongoAutoConfiguration, MongoDataConfiguration

    @ConditionalOnMissingBean
    @Bean
    public MongoClient commentMongoClient(MongoProperties mongoProperties) {
        return MongoClients.create(mongoProperties.getUri());
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient mongoClient,
                                               MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getMongoClientDatabase());
    }

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTransactionManager mongoTransactionManager = new MongoTransactionManager(mongoDatabaseFactory);
        mongoTransactionManager.setOptions(TransactionOptions.builder()
                //多数写
                .writeConcern(WriteConcern.MAJORITY)
                //快照隔离 , 适用于事务
                .readConcern(ReadConcern.SNAPSHOT)
                //事务必须读主节点
                .readPreference(ReadPreference.primary()).build());
        return mongoTransactionManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new BigDecimalToDecimal128Converter());
        converterList.add(new Decimal128ToBigDecimalConverter());
        converterList.add(new EnumToIntegerConverter());
        converterList.add(new IntegerToEnumConvertFactory.IntegerToEnumConverter<>(null));
        return new MongoCustomConversions(converterList);
    }

    @SuppressWarnings("all")
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory,
                                       MappingMongoConverter converter) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory, converter);
        //跨库事务需要开启
        //mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();
        GenericConversionService conversionService = (GenericConversionService) converter.getConversionService();
        conversionService.addConverterFactory(new IntegerToEnumConvertFactory());
        return mongoTemplate;
    }

}
