package com.losaxa.core.pattern.chain;

/**
 * 抽象责任链
 * <a>https://refactoringguru.cn/design-patterns/chain-of-responsibility</a>
 * @param <T>
 */
public abstract class AbstractChain<T extends ChainContext> implements Chain<T> {

    private Chain<T> nextChain;

    public Chain<T> setNext(Chain<T> nextChain) {
        if (nextChain == null) {
            throw new IllegalArgumentException("param nextChain must be not null");
        }
        this.nextChain = nextChain;
        return nextChain;
    }

    @Override
    public T handle(T chainContext) {
        T r = preNext(chainContext);
        return nextChain != null && r != null && r.canNext() ? nextChain.handle(chainContext) : r;
    }

    abstract T preNext(T chainContext);

}
