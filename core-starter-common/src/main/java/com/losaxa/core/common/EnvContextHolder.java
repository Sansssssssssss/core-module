package com.losaxa.core.common;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Spring Environment 上下文
 */
@Component
public class EnvContextHolder implements EnvironmentAware {

    private static Environment env;

    public static <E> E getProperty(String property,
                             Class<E> clazz) {
        return env.getProperty(property, clazz);
    }

    @Override
    public void setEnvironment(Environment environment) {
        EnvContextHolder.env = environment;
    }
}
