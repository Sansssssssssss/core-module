package com.losaxa.core.pattern.strategy;

public interface Strategy<T, R> {

    boolean supports(T param);

    R execute(T param);
}
