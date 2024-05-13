package com.baidu.fsg.uid.config;

import com.baidu.fsg.uid.BaiduUidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baidu.fsg.uid.worker.service.WorkerNodeService;
import com.losaxa.core.mongo.BaseMongoRepository;
import com.losaxa.core.mongo.BaseMongoRepositoryFactoryBean;
import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.baidu.fsg.uid.worker.dao"
        ,mongoTemplateRef = "uidMongoTemplate"
        , repositoryBaseClass = BaseMongoRepository.class
        , repositoryFactoryBeanClass = BaseMongoRepositoryFactoryBean.class)
@Configuration
@Import(WorkerNodeService.class)
public class UidGeneratorConfig {

    @Bean
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner(){
        return new DisposableWorkerIdAssigner();
    }

    @Bean
    public BaiduUidGenerator uidGenerator() {
        return new DefaultUidGenerator();
    }

    @Bean("uidMongoTemplate")
    public MongoTemplate test2MongoTemplate(MongoClient mongoClient,
                                            MappingMongoConverter converter) {
        MongoTemplate uidTemplate = new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(mongoClient, "uid_generator"), converter);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return uidTemplate;
    }

}
