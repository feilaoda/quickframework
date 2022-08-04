package com.quickpaas.framework.service;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.filter.QueryFilter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 */
public interface BaseQuickService<T extends BaseDomain>  {

    List<T> selectEntitiesByIds(List<String> selectColumns, String idsColumn, Collection<Serializable> ids);
    List<T> selectEntitiesByIds(QueryField field, String column, Collection<Serializable> ids);
    String getColumnName(Class<?> targetDomainClass, String column);

    void fillJoinRelations(List<? extends BaseDomain> entities, QueryField queryField, JoinRelation join);
    List<T> selectList(List<QueryFilter> filters, List<String> columns);
    T selectOne(QueryRequest request);
    List<T> selectList(QueryRequest request);
    int removeByColumn(String column, Object value);
    void createOrSave(BaseDomain entity);
}
