package com.losaxa.core.pattern.factory;

/**
 * 工厂的主要职责并非是创建产品。其中通常会包含一些核心业务逻辑，这些逻辑依赖于由工厂方法返回的产品对象。子类可通过重写工厂方法并使其返回不同类型的产品来间接修改业务逻辑
 * @param <T>
 * @param <R>
 */
public abstract class AbstractFactory<T,R> implements Factory<Product<T,R>> {

    public R use(T param){
        //这里可以处理其他公共业务
        Product<T, R> product = getObject();
        R rs = product.use(param);
        //这里可以处理其他公共业务
        return rs;
    }



}
