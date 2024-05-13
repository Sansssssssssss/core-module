package com.losaxa.core.common.converter;

import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * mapstruct 类型转换基础接口
 * @param <S>
 * @param <T>
 */
public interface ObjectConverter<S,T> {

    T to(S source);

    List<T> to(List<S> sources);

    void to(S source, @MappingTarget T target);

    S from(T target);

    List<S> from(List<T> target);

    void from(T target, @MappingTarget S source);

}
