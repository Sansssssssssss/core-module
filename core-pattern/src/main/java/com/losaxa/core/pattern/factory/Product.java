package com.losaxa.core.pattern.factory;

/**
 * 产品接口
 *
 * @param <T>
 * @param <R>
 */
public interface Product<T, R> {

    /**
     * 使用产品(抽象)
     *
     * @param param
     * @return
     */
    R use(T param);

}
