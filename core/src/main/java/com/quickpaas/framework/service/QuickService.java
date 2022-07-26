package com.quickpaas.framework.service;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.quickql.Query;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.filter.QueryFilter;

import java.util.Collection;
import java.util.List;

/**
 */
public interface QuickService<E extends BaseEntity, D extends BaseDomain<E>>  {

    List<E> selectEntitiesByIds(List<String> selectColumns, String idsColumn, Collection<Long> ids);
    List<E> selectEntitiesByIds(QueryField field, String column, Collection<Long> ids);
    String getColumnName(Class<?> targetDomainClass, String column);

    void fillJoinRelations(List<D> entities, QueryField queryField, JoinRelation join);
    List<E> selectList(List<QueryFilter> filters, List<String> columns);
    E selectOne(QueryRequest request);
    List<E> selectList(QueryRequest request);
    void removeByColumn(String column, Object value);
    void createOrSave(BaseEntity entity);
}
