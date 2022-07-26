package com.quickpaas.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.RelationType;
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.framework.quickql.*;
import com.quickpaas.framework.service.BaseService;
import com.quickpaas.framework.service.QuickService;
import com.quickpaas.framework.service.QueryRepository;
import com.quickpaas.framework.utils.ClassUtils;
import com.quickpaas.framework.utils.NameUtils;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.framework.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public abstract class BaseServiceImpl<M extends BaseMapper<E>, E extends BaseEntity, D extends BaseDomain<E>> extends BaseQuickServiceImpl<M, E, D> implements BaseService<E, D> {

    @Autowired
    private ClassCache classCache;

    @Autowired
    private QueryRepository<E, D> queryRepository;

    private Class<E> entityClass;
    private Class<D> domainClass;

    public BaseServiceImpl() {
        entityClass = (Class<E>) ReflectionUtils.getSuperClassGenericType(this.getClass(), 1);
        domainClass = (Class<D>) ReflectionUtils.getSuperClassGenericType(this.getClass(), 2);
    }



    public List<D> findSimpletList(QueryRequest queryRequest) {
        if(queryRequest == null) {
            return new ArrayList<>();
        }
        queryRequest.setClz(domainClass);
        List<E> entityList = selectEntities(queryRequest);
        List<D> ds = new ArrayList<>();
        for (E e : entityList) {
            D newdto = (D)ClassUtils.newDomainInstance(domainClass);
            newdto = (D)newdto.from(e);
            ds.add(newdto);
        }
        fillJoinRelations(queryRequest, ds, queryRequest.getClz());
        return ds;
    }

    public List<E> selectEntities(QueryRequest request) {
        return selectList(request);
    }


    @Override
    public void removeByColumn(String column, Object value) {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        getBaseMapper().delete(queryWrapper);
    }

    @Override
    public void createOrSave(BaseEntity domain) {
        save((E)domain);
    }

    public IPage<E> findPage(QueryRequest request) {
        if(request == null) {
            return new Page<E>(1, 0);
        }
        QueryWrapper<E> wrapper = createQuery(request);
        Long pageIndex = 1L;
        Long pageSize = 10L;
        if(request.getPage() != null) {
            pageIndex = request.getPage().getCurrent();
            pageSize = request.getPage().getPageSize();
        }
        return page(new Page<>(pageIndex, pageSize), wrapper);
    }

    @Override
    public IPage<D> findPage(Query<D> query) {
        if(query == null) {
            return new Page<>(1, 0);
        }
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        IPage<E> entityPage = findPage(queryRequest);
        List<D> domains = ObjectUtils.map(entityPage.getRecords(), entityClass, domainClass);
        fillJoinRelations(new QueryRequest(), domains, domainClass);

        IPage<D> newPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        newPage.setRecords(domains);
        return newPage;
    }



    public List<E> selectBatchIds(List<Long> ids) {
        return baseMapper.selectBatchIds(ids);
    }

    @Override
    public void remove(Query<D> query) {
        QueryWrapper wrapper = new QueryWrapper();
        if(query.getWheres().isEmpty()) {
            //delete all data, forbidden
            return;
        }
        query.getWheres().forEach((key, value) -> {
            fillQueryWrapper(wrapper, value.getOp(), key, value.getValue());
        });
        baseMapper.delete(wrapper);
    }

    @Override
    public D findById(Long id) {
        E e = baseMapper.selectById(id);
        return ObjectUtils.map(e, domainClass);
    }

    @Override
    public D findOne(Query query) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        E entity = selectOne(queryRequest);
        if(entity == null) {
            return null;
        }
        D vo = ClassUtils.newDomainInstance(domainClass);
        vo = (D)vo.from(entity);
        fillJoinRelations(queryRequest, Lists.newArrayList(vo), domainClass);
        return vo;
    }

    @Override
    public List<D> findList(Query query) {
        if(query == null) {
            query = new Query();
        }
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        List<D> domains = findSimpletList(queryRequest);
        fillJoinRelations(queryRequest, domains, domainClass);
        return domains;
    }

    public D findSimpleOne(Query query) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        E entity = selectOne(queryRequest);
        D vo = ClassUtils.newDomainInstance(domainClass);
        vo = (D)vo.from(entity);
        return vo;
    }

    public List<D> findSimpleList(Query query) {
        if(query == null) {
            query = new Query();
        }
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        List<D> domains = findSimpletList(queryRequest);
        return domains;
    }

    @Override
    public List<D> findListByIds(Collection<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<E> values = baseMapper.selectBatchIds(ids);
        List<D> domains = ObjectUtils.map(values, entityClass, domainClass);
        fillJoinRelations(new QueryRequest(), domains, domainClass);
        return domains;
    }

    @Override
    public void beforeSave(D entity) {
        Map<String, JoinRelation> joins = classCache.getJoins(entity.getClass());
        joins.forEach((field, join) -> {
            if (join.getRelationType() != null && (RelationType.ManyToOne.equals(join.getRelationType()) || (RelationType.OneToOne.equals(join.getRelationType())))) {
                try {
                    String column = join.getJoins()[0];
                    Object columnValue = PropertyUtils.getProperty(entity, column);
                    Object value = PropertyUtils.getProperty(entity, field);
                    if (columnValue == null && value != null) {
                        BaseDomain<?> targetEntity = (BaseDomain<?>) value;
                        if (targetEntity.getId() != null) {
                            PropertyUtils.setProperty(entity, join.getJoins()[0], targetEntity.getId());
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new WebException(e);
                }
            }
        });
    }


    public void saveManyToMany(D entity) {
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
                            QuickService<? extends BaseEntity, ?> joinService = queryRepository.findService(joinClass);
                            if (joinService != null) {
                                joinService.removeByColumn(NameUtils.toColumnName(join.getJoins()[0]), entity.getId());
                                for (BaseDomain right : values) {
                                    BaseDomain<? extends BaseEntity> joiner = (BaseDomain) joinClass.newInstance();
                                    BaseEntity domain = joiner.to();
                                    PropertyUtils.setProperty(domain, join.getJoins()[0], entity.getId());
                                    PropertyUtils.setProperty(domain, join.getJoins()[1], right.getId());
                                    joinService.createOrSave(domain);
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
    public long count(Query<D> query) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(query, domainClass);
        QueryWrapper queryWrapper = createQuery(queryRequest);
        return baseMapper.selectCount(queryWrapper);
    }
}
