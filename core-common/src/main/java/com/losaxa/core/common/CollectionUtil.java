package com.losaxa.core.common;

import java.util.Collection;
import java.util.Map;

/**
 * 集合、数组工具
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(byte[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(short[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(int[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(long[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(float[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(double[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(char[] array) {
        return (array == null || array.length < 1);
    }

    public static boolean isEmpty(boolean[] array) {
        return (array == null || array.length < 1);
    }
}
