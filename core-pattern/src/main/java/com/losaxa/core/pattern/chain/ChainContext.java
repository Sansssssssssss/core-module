package com.losaxa.core.pattern.chain;

/**
 * 责任链上下文
 */
public class ChainContext {

    private boolean canNext = true;

    boolean canNext(){
        return canNext;
    }

    public void setCanNext(boolean canNext) {
        this.canNext = canNext;
    }

}
