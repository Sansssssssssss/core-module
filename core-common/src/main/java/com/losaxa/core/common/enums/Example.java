package com.losaxa.core.common.enums;

public enum Example implements IEnum<Integer> {

    example(1);

    private final int value;

    Example(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        throw new UnsupportedOperationException();
    }
}
