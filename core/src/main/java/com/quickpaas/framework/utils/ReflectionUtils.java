package com.quickpaas.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtils {
    private final static Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    public static <V> Class<V> getGenericClass(final Class<?> clazz) {
        ParameterizedType genericClazz = (ParameterizedType) clazz.getGenericSuperclass();
        if(genericClazz != null) {
            return (Class)genericClazz.getActualTypeArguments()[0];
        }else {
            return null;
        }
    }

    public static Class<?> getSuperClass(final Class<?> clazz) {
        return clazz.getSuperclass();
    }

    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            LOG.warn(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
            return Object.class;
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    LOG.warn(String.format("Warn: %s not set the actual class on superclass generic parameter", clazz.getSimpleName()));
                    return Object.class;
                } else {
                    return (Class) params[index];
                }
            } else {
                LOG.warn(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(), params.length));
                return Object.class;
            }
        }
    }

}


