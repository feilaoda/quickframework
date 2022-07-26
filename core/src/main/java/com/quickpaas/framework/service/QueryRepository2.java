package com.quickpaas.framework.service;

import com.google.common.collect.Lists;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.RelationType;
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.quickql.config.QuickqlProperty;
import com.quickpaas.framework.quickql.enums.QueryOp;
import com.quickpaas.framework.quickql.field.ObjectField;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.field.QueryOpField;
import com.quickpaas.framework.quickql.field.SimpleField;
import com.quickpaas.framework.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QueryRepository2<E extends BaseEntity, D extends BaseDomain<E>> {
    private final ServiceRegistry serviceRegistry;
    private final ClassCache classCache;

    private final String charEncoding = "UTF-8";

    private final boolean useAnsiQuotedIdentifiers = false;
    private final CharsetEncoder charsetEncoder;

    @Autowired
    private QuickqlProperty quickqlConfig;

    @Autowired
    public QueryRepository2(ServiceRegistry serviceRegistry, ClassCache classCache) {
        this.serviceRegistry = serviceRegistry;
        this.classCache = classCache;
        charsetEncoder = Charset.forName(this.charEncoding).newEncoder();
    }

    public static byte[] getBytes(String value) {
        return value.getBytes();
    }






    private Map<Long, BaseDomain<?>> findTargetEntitiesByIds(JoinRelation join, QueryField field, List<Long> ids) {
        return findTargetEntitiesByIds(join, field, "id", ids);
    }

    private Map<Long, BaseDomain<?>> findTargetEntitiesByIds(JoinRelation join, QueryField field, String columnName, List<Long> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        QuickService<?,?> quickService = findService(join.getTarget());
        List<? extends BaseEntity> entities = quickService.selectEntitiesByIds(field, quickService.getColumnName(join.getTarget(), columnName), ids);
        Map<Long, BaseDomain<?>> refDomains = new LinkedHashMap<>();
        entities.forEach(entity -> {
            try {
                BaseDomain target = (BaseDomain<?>) join.getTarget().newInstance();
                BaseDomain<?> domain = target.from(entity);
                refDomains.put(entity.getId(), domain);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        });
        return refDomains;
    }

    private Map<Long, List<Long>> findMiddleEntitiesByIds(JoinRelation join, Collection<Long> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        Class<?> targetDomainClass = join.getMiddle();
        QuickService<?, ?> service = findService(targetDomainClass);
        String field0 = join.getJoins()[0];
        String field1 = join.getJoins()[1];

        String column0 = classCache.getColumnName(targetDomainClass, field0);
        String column1 = classCache.getColumnName(targetDomainClass, field1);

        List<String> columns = Lists.newArrayList(column0, column1);

        List<?> entities = service.selectEntitiesByIds(columns, column0, ids);
        Map<Long, List<Long>> refEntities = new HashMap<>();
        entities.forEach(domain -> {
            try {
                if (StringUtils.isEmpty(join.getJoins()[1])) {
                    //error
                }
                Long key = (Long) ObjectUtils.getProperty(domain, field0);
                Long id = (Long) ObjectUtils.getProperty(domain, field1);
                if (key != null && id != null) {
                    if (refEntities.containsKey(key)) {
                        refEntities.get(key).add(id);
                    } else {
                        List<Long> values = new ArrayList<>();
                        values.add(id);
                        refEntities.put(key, values);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });

        return refEntities;
    }

    private Map<Long, BaseDomain<?>> findManyToOneEntities(JoinRelation join, QueryField field, Collection<Long> ids) {
        Map<Long, BaseDomain<?>> targetEntities = findTargetEntitiesByIds(join, field, new ArrayList<>(ids));
        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List targets = Lists.newArrayList(targetEntities.values());
                        QuickService<?,?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(targets, childQueryField, childJoin);
                    }
                }
            }
        }

        return targetEntities;
    }


    private Map<Long, List<BaseDomain<?>>> findOneToManyEntities(JoinRelation join, QueryField field, Collection<Long> ids) {
        String column = join.getJoins()[0];
        Map<Long, BaseDomain<?>> targetEntities = findTargetEntitiesByIds(join, field, column, new ArrayList<>(ids));
        Map<Long, List<BaseDomain<?>>> results = new HashMap<>();

        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List entityList = Lists.newLinkedList(targetEntities.values());
                        QuickService<?,?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(entityList, childQueryField, childJoin);
                    }
                }
            }
        }

        targetEntities.forEach((key, value) -> {
            Long sourceId = (Long) ObjectUtils.getProperty(value, column);
            if (!results.containsKey(sourceId)) {
                results.put(sourceId, new ArrayList<>());
            }
            results.get(sourceId).add(value);
        });

        return results;
    }

    private Map<Long, List<BaseDomain<?>>> findManyToManyEntities(JoinRelation join, QueryField field, Collection<Long> ids) {
        Map<Long, List<Long>> middleEntities = findMiddleEntitiesByIds(join, ids);

        Set<Long> targetIds = new HashSet<>();
        middleEntities.values().forEach(targetIds::addAll);
        Map<Long, BaseDomain<?>> targetEntities = findTargetEntitiesByIds(join, field, new ArrayList<>(targetIds));

        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List entityList = Lists.newLinkedList(targetEntities.values());
                        QuickService<?,?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(entityList, childQueryField, childJoin);
                    }
                }
            }
        }

        Set<Long> newIdSets = targetEntities.keySet();
        Map<Long, List<Long>> sortEntities = new LinkedHashMap<>();
        QueryOpField tmpOp = null;
        if (field instanceof ObjectField) {
            Optional<QueryOpField> opt = ((ObjectField) field).filterField(QueryOp.LIMIT);
            tmpOp = opt.orElse(null);
        }
        final QueryOpField limitOp = tmpOp;
        middleEntities.forEach((key, oldIds) -> {
            List<Long> newIds = new ArrayList<>();
            newIdSets.forEach(id -> {
                if (oldIds.contains(id)) {
                    newIds.add(id);
                }
            });
            if (limitOp != null) {
                int limit = Integer.parseInt(limitOp.getValue().toString());
                int max = Math.min(limit, newIds.size());
                List<Long> tmpIds = newIds.subList(0, max);
                sortEntities.put(key, tmpIds);
            } else {
                sortEntities.put(key, newIds);
            }
        });

        Map<Long, List<BaseDomain<?>>> results = new HashMap<>();
        sortEntities.forEach((key, values) -> {
            List<BaseDomain<?>> entities = new ArrayList<>();
            values.forEach(id -> {
                if (targetEntities.containsKey(id)) {
                    entities.add(targetEntities.get(id));
                }
            });
            results.put(key, entities);
        });
        return results;
    }

//    public List<? extends BaseEntity> queryEntityList(Class<?> domainClazz, List<QueryFilter> filters, List<String> columns) {
//        QuickService<?,?> service = findService(domainClazz);
//        return service.selectList(filters, columns);
//    }

    private void fillManyToOneFields(List<?> domains, QueryField field, JoinRelation join) {
        String column = join.getJoins()[0];
        Set<Long> ids = domains.stream().map(domain -> (Long) ObjectUtils.getProperty(domain, join.getJoins()[0])).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Long, BaseDomain<?>> targetDomains = findManyToOneEntities(join, field, ids);
            domains.forEach(domain -> {
                Long id = (Long) ObjectUtils.getProperty(domain, column);
                try {
                    BaseDomain<?> target = targetDomains.get(id);
                    if (target != null) {
                        ObjectUtils.setProperty(domain, field.getName(), target);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    private void fillOneToManyFields(List<D> entities, QueryField field, JoinRelation join) {
        Set<Long> ids = entities.stream().map(BaseDomain::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Long, List<BaseDomain<?>>> refEntities = findOneToManyEntities(join, field, ids);
            entities.forEach(entity -> {
                Long id = entity.getId();
                try {
                    List<?> refs = refEntities.get(id);
                    ObjectUtils.setProperty(entity, field.getName(), refs);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    private void fillManyToManyFields(List<D> entities, QueryField field, JoinRelation join) {
        Set<Long> ids = entities.stream().map(BaseDomain::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Long, List<BaseDomain<?>>> refEntities = findManyToManyEntities(join, field, ids);
            entities.forEach(entity -> {
                Long id = entity.getId(); // (Long)BeanUtils.getProperty(entity, join.joinColumns()[0]);
                try {
                    List<?> refs = refEntities.get(id);
                    ObjectUtils.setProperty(entity, field.getName(), refs);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }


    public void fillJoinRelations(QueryRequest queryRequest, List<D> entities, Class<?> clz) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        Map<String, JoinRelation> joins = classCache.getJoins(clz);
        if (quickqlConfig.isAutoQueryAllFields() && CollectionUtils.isEmpty(queryRequest.getFields())) {
            joins.forEach((field, join) -> fillJoinRelations(entities, new SimpleField(field), join));
        } else {
            for (QueryField field : queryRequest.getFields()) {
                if (joins.containsKey(field.getName())) {
                    JoinRelation join = joins.get(field.getName());
                    fillJoinRelations(entities, field, join);
                }
            }
        }
    }


    public void fillJoinRelations(List<D> entities, QueryField field, JoinRelation join) {
        if (join.getRelationType().equals(RelationType.ManyToOne) || join.getRelationType().equals(RelationType.OneToOne)) {
            fillManyToOneFields(entities, field, join);
        } else if (join.getRelationType().equals(RelationType.ManyToMany)) {
            fillManyToManyFields(entities, field, join);
        } else if (join.getRelationType().equals(RelationType.OneToMany)) {
            fillOneToManyFields(entities, field, join);
        }
    }


    public void fillJoinRelations(List<D> entities, Class<?> clz) {
        Map<String, JoinRelation> joins = classCache.getJoins(clz);
        if (joins != null) {
            joins.forEach((fieldName, join) -> {
                SimpleField simpleField = new SimpleField(fieldName);
                fillJoinRelations(entities, simpleField, join);
            });
        }
    }

    public QuickService<E, D> findService(Class<?> clz) {
        Class<?> clazz = clz.getSuperclass();
        return serviceRegistry.findService(clazz.getSimpleName());
    }

}
