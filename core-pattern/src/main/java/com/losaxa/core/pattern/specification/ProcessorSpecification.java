package com.losaxa.core.pattern.specification;

import java.util.function.Consumer;

/**
 * 扩展规约
 * @param <T>
 */
public interface ProcessorSpecification<T> extends Specification<T> {

    /**
     * 当不合法时的额外处理
     * @param t
     * @param consumer
     * @param <R>
     */
    <R> void negatedProcess(T t, Consumer<R> consumer);

}
