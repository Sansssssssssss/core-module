package com.losaxa.core.pattern.strategy;

import java.util.ArrayList;
import java.util.List;

public class Context<T, R> {

    private final List<Strategy<T, R>> strategyList = new ArrayList<>();

    public void add(Strategy<T, R> strategy) {
        this.strategyList.add(strategy);
    }

    R executeStrategy(T param) {
        return strategyList.stream().filter(e -> e.supports(param)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("没有相关策略"))
                .execute(param);
    }

}
