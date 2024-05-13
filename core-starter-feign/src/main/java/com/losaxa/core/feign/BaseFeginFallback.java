package com.losaxa.core.feign;

import com.losaxa.core.web.Result;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Feign Fallback 动态代理
 * @param <T>
 */
public class BaseFeginFallback<T> implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BaseFeginFallback.class);

    private final Class<T>  targetType;
    private final String    targetName;
    private final Throwable cause;

    public BaseFeginFallback(Class<T> targetType, String targetName, Throwable cause) {
        this.targetType = targetType;
        this.targetName = targetName;
        this.cause = cause;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String errMsg = cause.getMessage();
        logger.error("{} {}#{} fallback:{}", targetName, targetType.getName(), method.getName(), errMsg);
        Class<?> returnType = method.getReturnType();
        if (Result.class != returnType) {
            logger.warn("baseFallback returnType illegal");
            return null;
        }
        if (!(cause instanceof FeignException)) {
            return Result.fail999(errMsg);
        }
        FeignException exception = (FeignException) cause;
        String         content   = exception.contentUTF8();
        if (StringUtils.isEmpty(content)) {
            return Result.fail999(errMsg);
        }
        return Result.fallback(content);
    }

}
