package com.quickpaas.framework.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.RelationType;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.persistence.annotation.*;
import com.quickpaas.framework.utils.NameUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ClassCache {
    private Cache<Class<?>, Map<String, JoinRelation>> joinsCache;
    private Cache<Class<?>, Map<String, Field>> fieldsCache;
    private Cache<Class<?>, Map<String, String>> columnsCache;

    private Map<String, Class<?>> classesCache;

    public ClassCache() {
        joinsCache = CacheBuilder.newBuilder().build();
        fieldsCache = CacheBuilder.newBuilder().build();
        columnsCache = CacheBuilder.newBuilder().build();
        classesCache = new HashMap();
    }

    public void add(String className, Class<?> clz) {
        classesCache.put(className, clz);
    }

    public void addColumns(Class<?> clz, Map<String, String> columns) {
        columnsCache.put(clz, columns);
    }

    public String getColumnName(Class<?> clz, String fieldName) {
        Map<String, String> columns = columnsCache.getIfPresent(clz);
        if(columns != null && columns.containsKey(fieldName)) {
            return columns.get(fieldName);
        }else {
            return NameUtils.toColumnName(fieldName);
        }
    }

    public Class<?> findClass(String name) {
        return classesCache.get(name);
    }

    public Map<String, Field> getFields(Class<?> clz) {
        try {
            return fieldsCache.get(clz, () -> {
                List<Field> fieldList = new ArrayList<>();
                Map<String, Field> fieldEntities = new HashMap<>();
                fieldList.addAll(Arrays.asList(clz.getDeclaredFields()));
                fieldList.addAll(Arrays.asList(clz.getSuperclass().getDeclaredFields()));
                fieldList.addAll(Arrays.asList(clz.getSuperclass().getSuperclass().getDeclaredFields()));
                fieldList.forEach(field -> {
                    fieldEntities.put(field.getName(), field);
                });
                return fieldEntities;
            });
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return new HashMap<>();
    }

    public Map<String, JoinRelation> getJoins(Class<?> clz) {
        try {
            return joinsCache.get(clz, () -> {
                Field[] fields = clz.getDeclaredFields();
                Map<String, JoinRelation> joinEntities = new HashMap<>();
                Arrays.stream(fields).forEach(field -> {
                    Join join = field.getAnnotation(Join.class);
                    if (join != null) {
                        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
                        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                        JoinRelation relation = new JoinRelation();
                        if(manyToMany != null) {
                            relation.setRelationType(RelationType.ManyToMany);
                            if(manyToMany.targetDomain().equals(void.class)) {
                                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                                if(parameterizedType != null) {
                                    relation.setTarget((Class<?>)parameterizedType.getActualTypeArguments()[0]);
                                }else {
                                    throw new QuickException("Field is not GenericType");
                                }
                            }else {
                                relation.setTarget(manyToMany.targetDomain());
                            }
                            relation.setMiddle(manyToMany.middleDomain());
                            relation.setJoins(new String[] {manyToMany.leftMappedField(), manyToMany.rightMappedField()});
                        }else if(manyToOne != null) {
                            relation.setRelationType(RelationType.ManyToOne);
                            if(manyToOne.targetDomain().equals(void.class)) {
                                relation.setTarget(field.getType());
                            }else {
                                relation.setTarget(manyToOne.targetDomain());
                            }
                            relation.setJoins(new String[]{manyToOne.mappedField()});
                        }else if(oneToMany != null) {
                            relation.setRelationType(RelationType.OneToMany);
                            if(oneToMany.targetDomain().equals(void.class)) {
                                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                                if(parameterizedType != null) {
                                    relation.setTarget((Class<?>)parameterizedType.getActualTypeArguments()[0]);
                                }else {
                                    throw new QuickException("Field is not GenericType");
                                }
                            }else {
                                relation.setTarget(oneToMany.targetDomain());
                            }
                            relation.setJoins(new String[]{oneToMany.mappedField()});
                        }else if(oneToOne != null) {
                            relation.setRelationType(RelationType.OneToOne);
                            if(oneToOne.targetDomain().equals(void.class)) {
                                relation.setTarget(field.getType());
                            }else {
                                relation.setTarget(oneToOne.targetDomain());
                            }
                            relation.setJoins(new String[]{oneToOne.mappedField()});
                        }else {
                            throw new QuickException("Relationship config error, class: {}, field: {}", clz.getName(), field.getName());
                        }
                        joinEntities.put(field.getName(), relation);
                    }
                });
                return joinEntities;
            });
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return new HashMap<>();
    }
}
