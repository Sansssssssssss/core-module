package com.losaxa.core.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring容器上下文
 */
@Component
public class AppContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Autowired
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        AppContextHolder.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

}
