package com.quickpaas.framework.utils;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.persistence.annotation.Join;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.quickpaas.framework.exception.WebErrorCode.CREATE_DTO_ERROR;

@Slf4j
public class ClassUtils {


    public static <T extends BaseDomain> T newGenericSuperClassInstance(final Class<?> clazz, int superClassIndex) {
        Class<T> clz = (Class<T>) ReflectionUtils.getSuperClassGenericType(clazz, superClassIndex);
        try {
            return clz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        throw new QuickException(CREATE_DTO_ERROR);
    }

    public static <T> T newDomainInstance(Class<T> clz) {
        try {
            return clz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        throw new QuickException(CREATE_DTO_ERROR);
    }

    public static Map<String, Join> getJoinEntities(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        Map<String, Join> joinEntities = new HashMap<>();
        Arrays.stream(fields).forEach(field -> {
            Join join = field.getAnnotation(Join.class);
            if(join != null) {
                joinEntities.put(field.getName(), join);
            }
        });
        return joinEntities;
    }


}
