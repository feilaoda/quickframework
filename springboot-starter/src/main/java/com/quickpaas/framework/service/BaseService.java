package com.quickpaas.framework.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.quickql.Query;

import java.util.Collection;
import java.util.List;

/**
 */
public interface BaseService<E extends BaseEntity, D extends BaseDomain<E>> extends IService<E> {
    void beforeSave(D dto);
    void saveManyToMany(D entity);
    void remove(Query<D> query);
    void removeByColumn(String column, Object value);
    void createOrSave(E domain);
    D findById(Long id);
    D findOne(Query<D> query);
    List<D> findList(Query<D> query);
    IPage<D> findPage(Query<D> query);
    List<D> findListByIds(Collection<Long> ids);
    D findSimpleOne(Query<D> query);
    List<D> findSimpleList(Query<D> query);
    long count(Query<D> query);
}
