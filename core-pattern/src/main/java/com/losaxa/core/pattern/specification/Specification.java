package com.losaxa.core.pattern.specification;

import java.io.Serializable;

/**
 * 规约模式
 * @param <T>
 */
public interface Specification<T> extends Serializable {

    boolean isAffirmed(T t);

    default Specification<T> and(Specification<T> specification) {
        return Specification.and(this, specification);
    }

    default Specification<T> or(Specification<T> specification) {
        return Specification.or(this, specification);
    }

    default Specification<T> not(Specification<T> specification) {
        return t -> !specification.isAffirmed(t);
    }

    static <T> Specification<T> and(Specification<T> s1, Specification<T> s2) {
        return t -> s1.isAffirmed(t) && s2.isAffirmed(t);
    }

    static <T> Specification<T> or(Specification<T> s1, Specification<T> s2) {
        return t -> s1.isAffirmed(t) || s2.isAffirmed(t);
    }

}
