package com.quickpaas.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.domain.RelationType;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.framework.quickql.Query;
import com.quickpaas.framework.quickql.QueryParser;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.service.BaseQuickService;
import com.quickpaas.framework.service.BaseService;
import com.quickpaas.framework.utils.NameUtils;
import com.quickpaas.framework.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseDomain> extends BaseQuickServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    private ClassCache classCache;

    private Class<?> domainClass;

    public BaseServiceImpl() {
        domainClass = ReflectionUtils.getSuperClassGenericType(this.getClass(), 1);
    }



    public List<T> findSimpletList(QueryRequest queryRequest) {
        if(queryRequest == null) {
            return new ArrayList<>();
        }
        queryRequest.setClz(domainClass);
        List<T> entityList = selectEntities(queryRequest);

        fillJoinRelations(queryRequest, entityList, queryRequest.getClz());
        return entityList;
    }

    public List<T> selectEntities(QueryRequest request) {
        return selectList(request);
    }



    public Page<T> findPage(QueryRequest request) {
        if(request == null) {
            return new Page<T>(1, 0);
        }

        Long pageIndex = 1L;
        Long pageSize = 10L;
        if(request.getPage() != null) {
            pageIndex = request.getPage().getCurrent();
            pageSize = request.getPage().getPageSize();
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> resPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex, pageSize);
        QueryWrapper<T> wrapper = createQuery(request);
        IPage<T> iPage = page(resPage, wrapper);
        Page<T> res = new Page<>(iPage.getCurrent(), iPage.getSize(), iPage.getTotal());
        res.setRecords(iPage.getRecords());
        return res;
    }

    public Page<T> findPage(Query query) {
        if(query == null) {
            return new Page<>(1, 0);
        }
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        Page<T> entityPage = findPage(queryRequest);
        fillJoinRelations(queryRequest, entityPage.getRecords(), domainClass);

        return entityPage;
    }



    public List<T> selectBatchIds(List<Long> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public int remove(Query query) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if(query.getWhere().isEmpty()) {
            //delete all data, forbidden
            return 0;
        }
        query.getWhere().forEach((key, value) -> {
            //TODO
//            fillQueryWrapper(wrapper, value.getOp(), key, value.getValue());
        });
        return baseMapper.delete(wrapper);
    }

    @Override
    public T findById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    public T findOne(Query query) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        T entity = selectOne(queryRequest);
        if(entity == null) {
            return null;
        }
        fillJoinRelations(queryRequest, Lists.newArrayList(entity), domainClass);
        return entity;
    }

    @Override
    public List<T> findList(Query query) {
        if(query == null) {
            query = new Query();
        }
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        List<T> domains = findSimpletList(queryRequest);
        fillJoinRelations(queryRequest, domains, domainClass);
        return domains;
    }

//    public T findSimpleOne(Query query) {
//        QueryParser parser = new QueryParser(classCache);
//        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
//        return selectOne(queryRequest);
//    }

//    public List<T> findSimpleList(Query query) {
//        if(query == null) {
//            query = new Query();
//        }
//        QueryParser parser = new QueryParser(classCache);
//        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
//        return findSimpletList(queryRequest);
//    }

    @Override
    public List<T> findListByIds(Collection<Serializable> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<T> values = baseMapper.selectBatchIds(ids);
        fillJoinRelations(new QueryRequest(), values, domainClass);
        return values;
    }

    @Override
    public void beforeSave(T entity) {
        Map<String, JoinRelation> joins = classCache.getJoins(entity.getClass());
        joins.forEach((field, join) -> {
            if (join.getRelationType() != null && (RelationType.ManyToOne.equals(join.getRelationType()) || (RelationType.OneToOne.equals(join.getRelationType())))) {
                try {
                    String column = join.getJoins()[0];
                    Object columnValue = PropertyUtils.getProperty(entity, column);
                    Object value = PropertyUtils.getProperty(entity, field);
                    if (columnValue == null && value != null && value instanceof BaseDomain) {
                        BaseDomain targetEntity = (BaseDomain) value;
                        if (targetEntity.tid() != null) {
                            PropertyUtils.setProperty(entity, join.getJoins()[0], targetEntity.tid());
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new WebException(e);
                }
            }
        });
    }


    public void saveManyToMany(T entity) {
        Map<String, JoinRelation> joins = classCache.getJoins(entity.getClass());
        if (joins.size() > 0) {
            joins.forEach((key, join) -> {
                try {
                    RelationType relationType = join.getRelationType();
                    if (relationType.equals(RelationType.ManyToMany)) {
                        Object value = PropertyUtils.getProperty(entity, key);
                        if (value != null) {
                            Class<?> joinClass = join.getMiddle();
                            List<BaseDomain> values = (List) value;
                            BaseQuickService<? extends BaseDomain> joinService = findService(joinClass);
                            if (joinService != null) {
                                joinService.removeByColumn(NameUtils.toColumnName(join.getJoins()[0]), entity.tid());
                                for (BaseDomain right : values) {
                                    BaseDomain joiner = (BaseDomain) joinClass.newInstance();
                                    PropertyUtils.setProperty(joiner, join.getJoins()[0], entity.tid());
                                    PropertyUtils.setProperty(joiner, join.getJoins()[1], right.tid());
                                    joinService.createOrSave(joiner);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new QuickException(e);
                }
            });
        }
    }

    @Override
    public long count(Query query) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        QueryWrapper queryWrapper = createQuery(queryRequest);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }



    @Override
    public boolean save(T entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean updateById(T entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(T entity) {
        return super.removeById(entity);
    }

    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }


}
