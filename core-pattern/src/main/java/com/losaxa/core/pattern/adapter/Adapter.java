package com.losaxa.core.pattern.adapter;

/**
 * 适配器接口
 * 参考 springmvc HandlerAdapter 它与适配器模式思想是一样的,实现方式不太一样
 * <a>https://refactoringguru.cn/design-patterns/adapter</a>
 * @param <T>
 * @param <R>
 */
public interface Adapter<T,R> {

    boolean supports(T param);

    R adapt(T param);

}



