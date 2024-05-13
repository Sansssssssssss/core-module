package com.losaxa.core.pattern.chain;

/**
 * 责任链接口
 * @param <T>
 */
public interface Chain<T extends ChainContext> {

    T handle(T chainContext);

}
