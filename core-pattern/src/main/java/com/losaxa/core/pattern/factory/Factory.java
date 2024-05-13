package com.losaxa.core.pattern.factory;

/**
 * 工厂方法接口
 * <a>https://refactoringguru.cn/design-patterns/factory-method</a>
 * @param <T> 产品
 */
public interface Factory<T> {

    T getObject();

}
