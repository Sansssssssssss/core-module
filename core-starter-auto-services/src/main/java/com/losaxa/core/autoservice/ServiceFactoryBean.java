package com.losaxa.core.autoservice;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.util.Lazy;

/**
 * Service 组件工厂 Bean
 * @param <S>
 * @param <E>
 */
public class ServiceFactoryBean<S extends BaseService<E, ?, ?>, E> implements FactoryBean<S>, InitializingBean, BeanFactoryAware {

    private BaseServiceFactory baseServiceFactory;

    private final Class<? extends S> serviceInterface;

    private BeanFactory beanFactory;

    private boolean lazyInit = false;

    private Lazy<S> service;

    public ServiceFactoryBean(Class<? extends S> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public S getObject() {
        return service.get();
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public void afterPropertiesSet() {
        baseServiceFactory = new BaseServiceFactory<>(beanFactory);
        service = Lazy.of(() -> (S) baseServiceFactory.getService(serviceInterface));
        //service = (S) baseServiceFactory.getService(serviceInterface);
        if (!lazyInit) {
            this.service.get();
        }
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
