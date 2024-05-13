package com.losaxa.core.mongo;

import com.losaxa.core.mongo.config.CoreDbProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

/**
 * BaseMongoRepository FactoryBean
 *
 * @param <R>
 * @param <E>
 * @param <ID>
 */
public class BaseMongoRepositoryFactoryBean<R extends MongoRepository<E, ID>, E, ID extends Serializable>
        extends MongoRepositoryFactoryBean<R, E, ID> {


    public BaseMongoRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new BasicRepositoryFactory<>(operations);
    }


    private static class BasicRepositoryFactory<E, ID extends Serializable> extends MongoRepositoryFactory {

        private final MongoOperations mongoOperations;

        public BasicRepositoryFactory(MongoOperations mongoOperations) {
            super(mongoOperations);
            this.mongoOperations = mongoOperations;
        }

        @Override
        protected Object getTargetRepository(RepositoryInformation information) {
            MongoEntityInformation<?, Serializable> entityInformation   = getEntityInformation(information.getDomainType());
            Class<?>                                repositoryInterface = information.getRepositoryInterface();
            BaseMongoRepositoryImpl<E, ID>          baseMongoRepository = new BaseMongoRepositoryImpl<>((MongoEntityInformation<E, ID>) entityInformation, this.mongoOperations);
            LogicDelete                             logicDelete         = repositoryInterface.getAnnotation(LogicDelete.class);
            if (logicDelete != null) {
                baseMongoRepository.setLogicDeleteField(logicDelete.field());
            }
            return baseMongoRepository;
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseMongoRepositoryImpl.class;
        }

    }

}
