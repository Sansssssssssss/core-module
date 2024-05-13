package com.losaxa.core.common.enums;

/**
 * 枚举工具
 */
public class EnumUtils {

    private EnumUtils() {
    }

    public static <V, E extends Enum<?> & IEnum<V>> E valueOf(Class<E> enumClass, V value) {
        return getIenum(enumClass, value);
    }

    public static <V, E extends IEnum<V>> E getIenum(Class<E> enumClass, V value) {
        for (E status : enumClass.getEnumConstants()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }

}
