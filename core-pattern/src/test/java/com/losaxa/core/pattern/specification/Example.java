package com.losaxa.core.pattern.specification;

public class Example implements Specification<Integer> {

    @Override
    public boolean isAffirmed(Integer args) {
        return args != null;
    }

}
