package com.losaxa.core.autoservice;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.util.Assert;

/**
 * service 组件 bean 名称生成器
 */
public class ServiceBeanNameGenerator {

    private final BeanNameGenerator      generator;
    private final BeanDefinitionRegistry registry;

    private static BeanNameGenerator defaultBeanNameGenerator(BeanNameGenerator generator) {
        return generator == null || ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR.equals(generator)
                ? new AnnotationBeanNameGenerator()
                : generator;
    }

    public ServiceBeanNameGenerator(BeanNameGenerator generator,
                                    BeanDefinitionRegistry registry) {
        this.generator = defaultBeanNameGenerator(generator);
        this.registry = registry;
    }

    public String generateBeanName(BeanDefinition definition,
                                   Class<?> clazz) {
        AnnotatedBeanDefinition beanDefinition = definition instanceof AnnotatedBeanDefinition
                ? (AnnotatedBeanDefinition) definition
                : new AnnotatedGenericBeanDefinition(clazz);
        return generator.generateBeanName(beanDefinition, registry);
    }


}
