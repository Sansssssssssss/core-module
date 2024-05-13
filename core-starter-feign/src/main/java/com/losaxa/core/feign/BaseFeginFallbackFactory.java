package com.losaxa.core.feign;

import feign.Target;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * Feign FallbackFactory
 * @param <T>
 */
public class BaseFeginFallbackFactory<T> implements FallbackFactory<T> {

    private final Target<T> target;

    public BaseFeginFallbackFactory(Target<T> target) {
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T create(Throwable cause) {
        final Class<T> targetType = target.type();
        final String   targetName = target.name();
        Enhancer       enhancer   = new Enhancer();
        enhancer.setSuperclass(targetType);
        enhancer.setUseCache(true);
        enhancer.setCallbackFilter(method -> {
            if (method.getName().equals("toString")) {
                return 0;
            }
            return 1;
        });
        enhancer.setCallbacks(new Callback[]{NoOp.INSTANCE, new BaseFeginFallback<>(targetType, targetName, cause)});
        return (T) enhancer.create();
    }
}
