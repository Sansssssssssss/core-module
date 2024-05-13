package com.losaxa.core.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

/**
 * 反射工具
 */
public class ReflectUtil {

    private ReflectUtil() {
    }

    /**
     * 获取方法注解的属性
     */
    public static Map<String, Object> getAnnotationProperties(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Class<? extends Annotation> annotationType) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        return getAnnotationProperties(method, annotationType);
    }

    /**
     * 获取方法注解的属性
     */
    public static Map<String, Object> getAnnotationProperties(Method method, Class<? extends Annotation> annotationType) throws Exception {
        Annotation annotation = method.getDeclaredAnnotation(annotationType);
        return getAnnotationProperties(annotation);
    }

    public static Map<String, Object> getAnnotationProperties(Annotation annotation) throws NoSuchFieldException, IllegalAccessException {
        InvocationHandler                  invocationHandler = Proxy.getInvocationHandler(annotation);
        Class<? extends InvocationHandler> field             = invocationHandler.getClass();
        Field                              operateType       = field.getDeclaredField("memberValues");
        operateType.setAccessible(true);
        return (Map<String, Object>) operateType.get(invocationHandler);
    }

    public static <T> T getAnnotationValue(Annotation annotation, String property) throws NoSuchFieldException, IllegalAccessException {
        Map<String, ?> properties = getAnnotationProperties(annotation);
        return (T) properties.get(property);
    }

    /**
     * 获取类的接口的第一个泛型类型
     */
    public static Class<?> extractInterfaceActualType(Class<?> clazz) {
        Type[]            types  = clazz.getGenericInterfaces();
        ParameterizedType target = null;
        Type[]            typeArray;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArray.length < 1) {
                    break;
                }
                for (Type t : typeArray) {
                    if (t instanceof TypeVariable || t instanceof WildcardType) {
                        break;
                    } else {
                        target = (ParameterizedType) type;
                        break;
                    }
                }
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

    /**
     * 获取类的父类的第一个泛型类型
     */
    public static Class<?> extractSupperActualType(Class<?> clazz) {
        Type              genericSuperclass = clazz.getGenericSuperclass();
        ParameterizedType target            = null;
        Type[]            typeArray;
        if (genericSuperclass instanceof ParameterizedType) {
            typeArray = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (typeArray.length < 1) {
                return null;
            }
            for (Type t : typeArray) {
                if (t instanceof TypeVariable || t instanceof WildcardType) {
                    break;
                } else {
                    target = (ParameterizedType) genericSuperclass;
                    break;
                }
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

}
