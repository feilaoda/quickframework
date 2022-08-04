package com.quickpaas.framework.service;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.quickql.Query;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 */
public interface BaseService<T> {
    void beforeSave(T dto);
    void saveManyToMany(T entity);
    boolean save(T entity);
    default boolean saveBatch(Collection<T> entityList) {
        return this.saveBatch(entityList, 1000);
    }

    boolean saveBatch(Collection<T> entityList, int batchSize);

    boolean updateById(T entity);
    T findById(Serializable id);
    int remove(Query query);
    boolean removeById(Serializable id);
    T findOne(Query query);
    List<T> findList(Query query);
    Page<T> findPage(Query query);
    List<T> findListByIds(Collection<Serializable> ids);
    long count(Query query);
}
