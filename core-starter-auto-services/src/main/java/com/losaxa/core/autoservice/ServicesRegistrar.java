package com.losaxa.core.autoservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态注册 service 实现类Bean
 */
@Slf4j
public class ServicesRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    /**
     * 注册 Bean BeanDefinition
     *
     * @param metadata
     * @param registry
     * @param generator
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry,
                                        BeanNameGenerator generator) {
        Assert.notNull(metadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
        //注解开启配置,如果需要自动配置提供默认注解即可,参考org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport.AutoConfiguredAnnotationRepositoryConfigurationSource
        if (metadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }
        String[]                 basePackages                   = getBasePackages(metadata);
        List<BeanDefinition>     serviceInterfaceBeanDefinition = scanCandidateComponents(basePackages);
        ServiceBeanNameGenerator serviceBeanNameGenerator       = new ServiceBeanNameGenerator(generator, registry);
        for (BeanDefinition beanDefinition : serviceInterfaceBeanDefinition) {
            Class<?> serviceInterface = getServiceInterface(beanDefinition);
            if (serviceInterface == null) {
                continue;
            }
            registerBeanDefinition(metadata, registry, serviceBeanNameGenerator, serviceInterface);
        }

    }

    private Class<?> getServiceInterface(BeanDefinition beanDefinition) {
        String   serviceInterfaceName = ConfigurationUtils.getRequiredBeanClassName(beanDefinition);
        Class<?> serviceInterface     = null;
        try {
            serviceInterface = ClassUtils.forName(serviceInterfaceName, resourceLoader.getClassLoader());
        } catch (ClassNotFoundException e) {
            log.warn("auto-service - Could not load type {} using class loader {}.", serviceInterfaceName, resourceLoader.getClassLoader(), e);
            return null;
        }
        return serviceInterface;
    }

    private void registerBeanDefinition(AnnotationMetadata metadata,
                                        BeanDefinitionRegistry registry,
                                        ServiceBeanNameGenerator generator,
                                        Class<?> serviceInterface) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServiceFactoryBean.class);
        builder.getRawBeanDefinition().setSource(metadata);
        builder.addConstructorArgValue(serviceInterface);
        builder.addPropertyValue("lazyInit", false);
        builder.setLazyInit(false);
        builder.setPrimary(false);
        AbstractBeanDefinition factoryBeanBeanDefinition = builder.getBeanDefinition();
        factoryBeanBeanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, serviceInterface);
        registry.registerBeanDefinition(generator.generateBeanName(factoryBeanBeanDefinition, serviceInterface), factoryBeanBeanDefinition);
    }

    private List<BeanDefinition> scanCandidateComponents(String[] basePackages) {
        ServiceComponentProvider scanner = new ServiceComponentProvider();
        scanner.setEnvironment(environment);
        scanner.setResourceLoader(resourceLoader);
        return Arrays.stream(basePackages).flatMap(it -> scanner.findCandidateComponents(it).stream()).collect(Collectors.toList());
    }

    private String[] getBasePackages(AnnotationMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(getAnnotation().getName());
        return (String[]) annotationAttributes.get("basePackages");
    }

    /**
     * 配置注解
     *
     * @return
     */
    private Class<? extends Annotation> getAnnotation() {
        return EnableAutoService.class;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        registerBeanDefinitions(importingClassMetadata, registry, null);
    }

}
