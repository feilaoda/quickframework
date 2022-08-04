package com.quickpaas.framework.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.RelationType;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.quickql.FieldType;
import com.quickpaas.framework.quickql.QueryBindValue;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.quickql.enums.AndOr;
import com.quickpaas.framework.quickql.enums.FilterOp;
import com.quickpaas.framework.quickql.enums.QueryOp;
import com.quickpaas.framework.quickql.field.ObjectField;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.field.QueryOpField;
import com.quickpaas.framework.quickql.field.SimpleField;
import com.quickpaas.framework.quickql.filter.AndOrQueryFilter;
import com.quickpaas.framework.quickql.filter.QueryFilter;
import com.quickpaas.framework.quickql.filter.RelationQueryFilter;
import com.quickpaas.framework.quickql.filter.SimpleValueQueryFilter;
import com.quickpaas.framework.service.BaseQuickService;
import com.quickpaas.framework.service.ServiceRegistry;
import com.quickpaas.framework.utils.NameUtils;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.framework.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseQuickServiceImpl<M extends BaseMapper<T>, T extends BaseDomain> extends ServiceImpl<M, T> implements BaseQuickService<T> {

    @Autowired
    private ClassCache classCache;
    @Autowired
    private ServiceRegistry serviceRegistry;

    public BaseQuickServiceImpl() {
    }


    public T selectOne(QueryRequest request) {
        if(request == null) {
            return null;
        }
        QueryWrapper<T> wrapper = createQuery(request);
        T data = baseMapper.selectOne(wrapper);
        return data;
    }

    public List<T> selectList(QueryRequest request) {
        if(request == null) {
            return new ArrayList<>();
        }
        QueryWrapper<T> wrapper = createQuery(request);
        List<T> data = list(wrapper);
        return data;
    }


    @Override
    public List<T> selectEntitiesByIds(List<String> selectColumns, String idsColumn, Collection<Serializable> ids) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(CollectionUtils.isNotEmpty(selectColumns)) {
            queryWrapper.select(selectColumns.toArray(new String[]{}));
        }
        queryWrapper.in(idsColumn, ids);
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    public List<T> selectEntitiesByIds(QueryField field, String idsColumn, Collection<Serializable> ids) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if(field instanceof ObjectField) {
            List<QueryOpField> opFields = ((ObjectField) field).getFields().stream().filter(e-> e instanceof QueryOpField).map(e->(QueryOpField)e).collect(Collectors.toList());
            for(QueryOpField opField: opFields) {
                int limit = 100000;
                int offset = 0;
                boolean hasLimit = false;
                if(QueryOp.ORDERBY.equals(opField.getOp())) {
                    if(opField.getValue() instanceof String) {
                        queryWrapper.orderByAsc(opField.getValue().toString());
                    }else if(opField.getValue() instanceof List) {
                        queryWrapper.orderByAsc((List)opField.getValue());
                    }
                }else if(QueryOp.ORDERBYDESC.equals(opField.getOp())) {
                    if(opField.getValue() instanceof String) {
                        queryWrapper.orderByDesc(opField.getValue().toString());
                    }else if(opField.getValue() instanceof List) {
                        queryWrapper.orderByDesc((List)opField.getValue());
                    }
                }
                if(hasLimit) {
                    queryWrapper.last("limit " + offset + "," + limit);
                }
            }
        }
        queryWrapper.in(idsColumn, ids);
        return getBaseMapper().selectList(queryWrapper);
    }


    public QueryWrapper<T> createQuery(QueryRequest request) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        fillQueryWrapper(request.getClass(), wrapper, request.getFilters());
        if (CollectionUtils.isNotEmpty(request.getOrderBy())) {
            List<String> orderBys = request.getOrderBy();
            for (String order : orderBys) {
                if (StringUtils.isNotEmpty(order)) {
                    if (order.startsWith("-")) {
                        wrapper.orderByDesc(order.substring(1));
                    } else {
                        wrapper.orderByAsc(order);
                    }
                }
            }
        }
        return wrapper;
    }



    private QueryWrapper<T> fillQueryWrapper(Class<?> targetClass,  List<QueryFilter> filters, List<String> columns) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (filters == null) {
            return wrapper;
        }
        if (CollectionUtils.isNotEmpty(columns)) {
            for (String col : columns) {
                wrapper.select(NameUtils.toColumnName(col));
            }
        }
        fillQueryWrapper(targetClass, wrapper, filters);
        return wrapper;
    }

    private void fillQueryWrapper(Class<?> targetClass, QueryWrapper<T> wrapper, List<QueryFilter> filters) {
        for (QueryFilter queryFilter : filters) {
            fillQueryWrapper(targetClass, wrapper, queryFilter);
        }
    }
    private void fillQueryWrapper(Class<?> targetClass, QueryWrapper<T> wrapper, QueryFilter queryFilter) {
        if (queryFilter instanceof AndOrQueryFilter) {
            if (((AndOrQueryFilter) queryFilter).getOp().equals(AndOr.AND)) {
                fillQueryWrapper(targetClass, wrapper, ((AndOrQueryFilter) queryFilter).getFilters());
            } else {
                wrapper.and(true, new Consumer<QueryWrapper<T>>() {
                            public void accept(QueryWrapper<T> queryWrapper) {
                                List<QueryFilter> filters = ((AndOrQueryFilter) queryFilter).getFilters();
                                if (filters.size() == 0) {
                                    return;
                                }
                                for (int i = 0; i < filters.size() - 1; i++) {
                                    fillQueryWrapper(targetClass, queryWrapper, filters.get(i));
                                    queryWrapper.or();
                                }
                                fillQueryWrapper(targetClass, queryWrapper, filters.get(filters.size() - 1));
                            }
                        }
                );
            }
        } else if (queryFilter instanceof SimpleValueQueryFilter) {
            fillSimpleQueryWrapper(targetClass, wrapper, (SimpleValueQueryFilter) queryFilter);
        } else if (queryFilter instanceof RelationQueryFilter) {
            fillRelationQueryWrapper(wrapper, (RelationQueryFilter) queryFilter);
        }
    }


//    public void fillQueryWrapper(QueryWrapper<?> wrapper, FilterOp op, String name, Object value) {
//        boolean condition = false;
//        if(value instanceof String) {
//            condition = StringUtils.isNotEmpty(value.toString());
//        }else {
//            condition = value != null;
//        }
//        String columnName = NameUtils.toColumnName(name);
//
//        if (op == null) {
//            op = FilterOp.EQ;
//        }
//        if (op.equals(FilterOp.EQ)) {
//            wrapper.eq(condition, columnName, value);
//        } else if (op.equals(FilterOp.LIKE)) {
//            wrapper.like(condition, columnName, value);
//        } else if (op.equals(FilterOp.IN) && value instanceof List) {
//            wrapper.in(columnName, value);
//        } else if (op.equals(FilterOp.ISNULL)) {
//            wrapper.isNull(columnName);
//        } else if (op.equals(FilterOp.NOTNULL)) {
//            wrapper.isNotNull(columnName);
//        } else if (op.equals(FilterOp.GT)) {
//            wrapper.gt(condition, columnName, value);
//        }else if (op.equals(FilterOp.LT)) {
//            wrapper.lt(condition, columnName, value);
//        }else if (op.equals(FilterOp.LE)) {
//            wrapper.le(condition, columnName, value);
//        }else if (op.equals(FilterOp.GE)) {
//            wrapper.ge(condition, columnName, value);
//        }else {
//            throw new QuickException("unknown filter op: " + op);
//        }
//    }

    private void fillSimpleQueryWrapper(Class<?> targetClass, QueryWrapper<T> wrapper, SimpleValueQueryFilter queryFilter) {//} QueryOp op, String name, Object value) {
        fillOpQueryWrapper(targetClass, wrapper, queryFilter.getOp(), queryFilter.getName(), queryFilter.getValue());
    }

    private void fillOpQueryWrapper(Class<?> targetClass, QueryWrapper<T> wrapper, FilterOp op, String fieldName, Object value) {
        boolean condition = false;

        condition = value != null && !StringUtils.isEmpty(value.toString());
        String columnName = classCache.getColumnName(targetClass, fieldName);

        if (op == null) {
            op = FilterOp.EQ;
        }
        if (op.equals(FilterOp.EQ)) {
            wrapper.eq(condition, columnName, value);
        } else if (op.equals(FilterOp.CONTAINS)) {
            wrapper.like(condition, columnName, value);
        } else if (op.equals(FilterOp.IN)) {
            wrapper.in(columnName, (List) value);
        } else if (op.equals(FilterOp.ISNULL)) {
            wrapper.isNull(columnName);
        } else if (op.equals(FilterOp.NOTNULL)) {
            wrapper.isNotNull(columnName);
        } else if (op.equals(FilterOp.GT)) {
            wrapper.gt(condition, columnName, value);
        } else if (op.equals(FilterOp.LT)) {
            wrapper.lt(condition, columnName, value);
        } else if (op.equals(FilterOp.LE)) {
            wrapper.le(condition, columnName, value);
        } else if (op.equals(FilterOp.GE)) {
            wrapper.ge(condition, columnName, value);
        }else if(op.equals(FilterOp.NE)) {
            wrapper.ne(condition, columnName, value);
        } else {
            throw new QuickException("未知的操作符: " + op);
        }
    }

    private void fillRelationQueryWrapper(QueryWrapper<T> wrapper, RelationQueryFilter relationFilter) {
        JoinRelation relation = relationFilter.getJoinRelation();
        if (relation.getRelationType().equals(RelationType.ManyToOne)) {
            Class<?> clz = relation.getTarget();
            List<String> cols = Lists.newArrayList("id");
            List<? extends BaseDomain> entities =   queryEntityList(clz, relationFilter.getFilters(), cols);
            List<Serializable> ids = entities.stream().map(BaseDomain::tid).collect(Collectors.toList());
            String field = relation.getJoins()[0];
            String column = classCache.getColumnName(clz, field);
            if (ids.size() > 0) {
                fillOpQueryWrapper(clz, wrapper, FilterOp.IN, column, ids);
            } else {
                wrapper.isNull(column);
            }
        } else if (relation.getRelationType().equals(RelationType.OneToMany)) {
            String column = relation.getJoins()[0];
            List<String> cols = Lists.newArrayList(column);
            Class<?> clz = relation.getTarget();
            List<? extends BaseDomain> entities = queryEntityList(clz, relationFilter.getFilters(), cols);
            List<Long> ids = entities.stream().map(e -> (Long) ObjectUtils.getProperty(e, column)).filter(Objects::nonNull).collect(Collectors.toList());
            if (ids.size() > 0) {
                fillOpQueryWrapper(clz, wrapper, FilterOp.IN, "id", ids);
            } else {
                wrapper.isNull("id");
            }
        } else if (relation.getRelationType().equals(RelationType.ManyToMany)) {
            String insql = buildMiddleSql(relationFilter, relation);
            wrapper.inSql("id", insql);
        }
    }


    private String buildMiddleSql(RelationQueryFilter queryFilter, JoinRelation join) {
        String leftColumn = join.getJoins()[0];
        String rightColumn = join.getJoins()[1];
        if (StringUtils.isEmpty(rightColumn)) {
            //error
            throw new QuickException("关联字段为空，{}", queryFilter.getClass().getName());
        }
        Class<?> targetClass = join.getTarget();
        Class<?> middleClass = join.getMiddle();
        StringBuilder sb = new StringBuilder();
        Class<?> targetTableClass = ReflectionUtils.getSuperClass(targetClass);
        Class<?> middleTableClass = ReflectionUtils.getSuperClass(middleClass);

        TableName targetTable = targetTableClass.getAnnotation(TableName.class);
        TableName middleTable = middleTableClass.getAnnotation(TableName.class);

        String leftAlias = "a";
        String rightAlias = "b";

        List<QueryFilter> fields = queryFilter.getFilters().stream().filter(e -> e instanceof SimpleValueQueryFilter || e instanceof AndOrQueryFilter).collect(Collectors.toList());

        List<String> whereSqls = new ArrayList<>();
        if (queryFilter.getFilters().size() == 1 && fields.size() == 1) {
            SimpleValueQueryFilter first = (SimpleValueQueryFilter) fields.get(0);
            if(first.getName().equals("id") && first.getOp().equals(FilterOp.EQ)) {
                String sqlFmt = "SELECT %s.%s FROM %s %s WHERE  ";
                String selectSql = String.format(sqlFmt,
                        leftAlias,
                        NameUtils.toColumnName(leftColumn),
                        middleTable.value(),
                        leftAlias
                );
                sb.append(selectSql);
                String where = leftAlias + "." + NameUtils.toColumnName(rightColumn) + " " + first.getOp().getOp();
                String v = first.getValue().toString();
                if (StringUtils.isNotEmpty(v)) {
                    where += " " + v;
                }
                sb.append(where);
                return sb.toString();
            }

        }
            String sqlFmt = "SELECT DISTINCT %s.%s FROM %s %s, %s %s WHERE  ";
            String selectSql = String.format(sqlFmt,
                    leftAlias,
                    NameUtils.toColumnName(leftColumn),
                    middleTable.value(),
                    leftAlias,
                    targetTable.value(),
                    rightAlias
            );

            sb.append(selectSql);

            String whereJoin = String.format("%s.%s = %s.id", leftAlias, NameUtils.toColumnName(rightColumn), rightAlias);
            whereSqls.add(whereJoin);

            int i = 0;
            List<String> wheres = new ArrayList<>();
            QueryBindValue[] bindValues = new QueryBindValue[fields.size()];
            for (QueryFilter filter : fields) {
                if (filter instanceof SimpleValueQueryFilter) {
                    SimpleValueQueryFilter simpleFilter = (SimpleValueQueryFilter) filter;
                    String sqlOp = convertSqlOp(simpleFilter.getOp());
                    StringBuffer buf = new StringBuffer();
                    buf.append(rightAlias).append(".")
                            .append(NameUtils.toColumnName(simpleFilter.getName()))
                            .append(" ").append(sqlOp).append(" ? ");
                    wheres.add(buf.toString());
                    setValue(bindValues, i++, simpleFilter);
                } else if (filter instanceof AndOrQueryFilter) {
                    AndOrQueryFilter andOrFilter = (AndOrQueryFilter) filter;
                    List<QueryFilter> subFilters = andOrFilter.getFilters();
                    QueryBindValue[] subBindValues = new QueryBindValue[subFilters.size()];
                    List<String> subWheres = new ArrayList<>();
                    int n = 0;
                    for (QueryFilter subFilter : subFilters) {
                        if (subFilter instanceof SimpleValueQueryFilter) {
                            SimpleValueQueryFilter simpleFilter = (SimpleValueQueryFilter) subFilter;
                            String sqlOp = convertSqlOp(simpleFilter.getOp());
                            StringBuffer buf = new StringBuffer();
                            buf.append(rightAlias).append(".")
                                    .append(NameUtils.toColumnName(simpleFilter.getName()))
                                    .append(" ").append(sqlOp).append(" ? ");
                            setValue(subBindValues, n++, simpleFilter);
                            subWheres.add(buf.toString());
                        }
                    }
                    n = 0;
                    List<String> subWhereSqls = new ArrayList<>();
                    for (String where : subWheres) {
                        String condition = where.replace("?", new String(subBindValues[n++].getBytesValue()));
                        subWhereSqls.add(condition);
                    }

                    String subWhereSql = "( " + String.join(" " + andOrFilter.getOp().name() + " ", subWhereSqls) + " )";
                    whereSqls.add(subWhereSql);
                }
            }
            i = 0;
            for (String where : wheres) {
                String condition = where.replace("?", new String(bindValues[i++].getBytesValue()));
                whereSqls.add(condition);
            }

            List<RelationQueryFilter> relationFilters = queryFilter.getFilters().stream().filter(e -> e instanceof RelationQueryFilter).map(e -> (RelationQueryFilter) e).collect(Collectors.toList());
            for (RelationQueryFilter filter : relationFilters) {
                String midSql = buildMiddleSql(filter, filter.getJoinRelation());
                String condsql = leftAlias + "." + NameUtils.toColumnName(rightColumn) + " IN ( " + midSql + " ) ";
                whereSqls.add(condsql);
            }
            if (!CollectionUtils.isEmpty(whereSqls)) {
                sb.append(String.join(" and ", whereSqls));
            }

        return sb.toString();
    }
    private String convertSqlOp(FilterOp op) {
        return op.getOp();
    }

    private String joinString(String s, List list) {
        List<String> newList = new ArrayList<>();
        list.forEach(e-> newList.add(e.toString()));
        return String.join(s, newList);
    }

    private void setValue(QueryBindValue[] values, int index, SimpleValueQueryFilter field) {
        if(field.getOp().equals(FilterOp.IN)) {
            if(field.getValue() instanceof List) {
                List l = new ArrayList();
                List vl = (List)field.getValue();
                for(Object v: vl) {
                    if(v instanceof String) {
                        l.add(StringEscape.escapeString(v.toString()));
                    }else {
                        l.add(v);
                    }
                }
                String value = "(" + joinString(",", l) + ")";
                setValue(values, index, value, FieldType.STRING);
            }else {
                throw new QuickException("查询条件中IN的值必须是列表");
            }
        }else
        if (field.getFieldType().equals(String.class)) {
            setString(values, index, field.getValue().toString());
        } else if (field.getFieldType().equals(Integer.class)) {
            setValue(values, index, String.valueOf(Integer.valueOf(field.getValue().toString())), FieldType.INTEGER);
        } else if (field.getFieldType().equals(Long.class)) {
            setValue(values, index, String.valueOf(Long.valueOf(field.getValue().toString())), FieldType.LONG);
        } else if(field.getFieldType().equals(Serializable.class)) {
            setString(values, index, field.getValue().toString());
        }else {
            throw new QuickException("未知的字段类型 {}", field.getFieldType().getName());
        }
     }

    public final synchronized void setValue(QueryBindValue[] values, int paramIndex, String val, FieldType type) {
        byte[] parameterAsBytes = getBytes(val);
        setValue(values, paramIndex, parameterAsBytes, type);
    }


    private void setValue(QueryBindValue[] values, int index, byte[] bytes, FieldType type) {
        QueryBindValue bindValue = new QueryBindValue();
        bindValue.setBytesValue(bytes);
        bindValue.setType(type);
        values[index] = bindValue;
    }

    private void setString(QueryBindValue[] values, int parameterIndex, String x) {
        byte[] parameterAsBytes;
        String parameterAsString = StringEscape.escapeString(x);
        parameterAsBytes = getBytes(parameterAsString);
        setValue(values, parameterIndex, parameterAsBytes, FieldType.STRING);
    }


    public List<? extends BaseDomain> queryEntityList(Class<?> domainClazz, List<QueryFilter> filters, List<String> columns) {
        BaseQuickService<?> service = findService(domainClazz);
        return service.selectList(filters, columns);
    }

    @Override
    public List<T> selectList(List<QueryFilter> filters, List<String> columns) {
        QueryWrapper<T> wrapper = fillQueryWrapper(entityClass, filters, columns);
        return list(wrapper);
    }

    @Override
    public String getColumnName(Class<?> targetDomainClass, String column) {
        Field field = null;
        try {
            field = targetDomainClass.getDeclaredField(column);
        } catch (NoSuchFieldException e) {
            try {
                field = targetDomainClass.getSuperclass().getDeclaredField(column);
            }catch (NoSuchFieldException e2) {
                try {
                    field = targetDomainClass.getSuperclass().getSuperclass().getDeclaredField(column);
                }catch (NoSuchFieldException e3) {

                }
            }
        }
        if(field != null) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField == null) {
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column);
            } else {
                return tableField.value();
            }
        }else {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column);
        }
    }

    @Override
    public int removeByColumn(String column, Object value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        return getBaseMapper().delete(queryWrapper);
    }

    @Override
    public void createOrSave(BaseDomain entity) {
        save((T)entity);
    }


    public static byte[] getBytes(String value) {
        return value.getBytes();
    }






    private Map<Serializable, ? extends BaseDomain> findTargetEntitiesByIds(JoinRelation join, QueryField field, List<Serializable> ids) {
        return findTargetEntitiesByIds(join, field, "id", ids);
    }

    private Map<Serializable, ? extends BaseDomain> findTargetEntitiesByIds(JoinRelation join, QueryField field, String columnName, List<Serializable> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        BaseQuickService<?> quickService = findService(join.getTarget());
        List<? extends BaseDomain> entities = quickService.selectEntitiesByIds(field, quickService.getColumnName(join.getTarget(), columnName), ids);
        Map<Serializable, BaseDomain> refDomains = new LinkedHashMap<>();
        entities.forEach(entity -> {
            try {
                refDomains.put(entity.tid(), entity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        });
        return refDomains;
    }

    private Map<Serializable, List<Serializable>> findMiddleEntitiesByIds(JoinRelation join, Collection<Serializable> ids) {
        if (ids.size() == 0) {
            return new HashMap<>();
        }
        Class<?> targetDomainClass = join.getMiddle();
        BaseQuickService<?> service = findService(targetDomainClass);
        String field0 = join.getJoins()[0];
        String field1 = join.getJoins()[1];

        String column0 = classCache.getColumnName(targetDomainClass, field0);
        String column1 = classCache.getColumnName(targetDomainClass, field1);

        List<String> columns = Lists.newArrayList(column0, column1);

        List<?> entities = service.selectEntitiesByIds(columns, column0, ids);
        Map<Serializable, List<Serializable>> refEntities = new HashMap<>();
        entities.forEach(domain -> {
            try {
                if (StringUtils.isEmpty(join.getJoins()[1])) {
                    //error
                }
                Serializable key = (Serializable) ObjectUtils.getProperty(domain, field0);
                Serializable id = (Serializable) ObjectUtils.getProperty(domain, field1);
                if (key != null && id != null) {
                    if (refEntities.containsKey(key)) {
                        refEntities.get(key).add(id);
                    } else {
                        List<Serializable> values = new ArrayList<>();
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

    private Map<Serializable, ? extends BaseDomain> findManyToOneEntities(JoinRelation join, QueryField field, Collection<Serializable> ids) {
        Map<Serializable, ? extends BaseDomain> targetEntities = findTargetEntitiesByIds(join, field, new ArrayList<>(ids));
        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List<? extends BaseDomain> targets = Lists.newArrayList(targetEntities.values());
                        BaseQuickService<?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(targets, childQueryField, childJoin);
                    }
                }
            }
        }

        return targetEntities;
    }


    private Map<Serializable, List<? extends BaseDomain>> findOneToManyEntities(JoinRelation join, QueryField field, Collection<Serializable> ids) {
        String column = join.getJoins()[0];
        Map<Serializable, ? extends BaseDomain> targetEntities = findTargetEntitiesByIds(join, field, column, new ArrayList<>(ids));
        Map<Serializable, List<? extends BaseDomain>> results = new HashMap<>();

        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List<? extends BaseDomain> entityList = Lists.newLinkedList(targetEntities.values());
                        BaseQuickService<?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(entityList, childQueryField, childJoin);
                    }
                }
            }
        }

        targetEntities.forEach((key, value) -> {
            Long sourceId = (Long) ObjectUtils.getProperty(value, column);
            if (!results.containsKey(sourceId)) {
                results.put(sourceId, Lists.newArrayList(value));
            }else {
                List domains = results.get(sourceId);
                domains.add(value);
            }
        });

        return results;
    }

    private Map<Serializable, List<? extends BaseDomain>> findManyToManyEntities(JoinRelation join, QueryField field, Collection<Serializable> ids) {
        Map<Serializable, List<Serializable>> middleEntities = findMiddleEntitiesByIds(join, ids);

        Set<Serializable> targetIds = new HashSet<>();
        middleEntities.values().forEach(targetIds::addAll);
        Map<Serializable, ? extends BaseDomain> targetEntities = findTargetEntitiesByIds(join, field, new ArrayList<>(targetIds));

        if (field instanceof ObjectField) {
            Class<?> target = join.getTarget();
            Map<String, JoinRelation> targetJoins = classCache.getJoins(target);
            for (QueryField childQueryField : ((ObjectField) field).getFields()) {
                if (childQueryField instanceof SimpleField || childQueryField instanceof ObjectField) {
                    if (targetJoins.containsKey(childQueryField.getName())) {
                        JoinRelation childJoin = targetJoins.get(childQueryField.getName());
                        List<? extends BaseDomain> entityList = Lists.newLinkedList(targetEntities.values());
                        BaseQuickService<?> targetService = findService(childJoin.getTarget());
                        targetService.fillJoinRelations(entityList, childQueryField, childJoin);
                    }
                }
            }
        }

        Set<Serializable> newIdSets = targetEntities.keySet();
        Map<Serializable, List<Serializable>> sortEntities = new LinkedHashMap<>();
        QueryOpField tmpOp = null;
        if (field instanceof ObjectField) {
            Optional<QueryOpField> opt = ((ObjectField) field).filterField(QueryOp.LIMIT);
            tmpOp = opt.orElse(null);
        }
        final QueryOpField limitOp = tmpOp;
        middleEntities.forEach((key, oldIds) -> {
            List<Serializable> newIds = new ArrayList<>();
            newIdSets.forEach(id -> {
                if (oldIds.contains(id)) {
                    newIds.add(id);
                }
            });
            if (limitOp != null) {
                int limit = Integer.parseInt(limitOp.getValue().toString());
                int max = Math.min(limit, newIds.size());
                List<Serializable> tmpIds = newIds.subList(0, max);
                sortEntities.put(key, tmpIds);
            } else {
                sortEntities.put(key, newIds);
            }
        });

        Map<Serializable, List<? extends BaseDomain>> results = new HashMap<>();
        sortEntities.forEach((key, values) -> {
            List<BaseDomain> entities = new ArrayList<>();
            values.forEach(id -> {
                if (targetEntities.containsKey(id)) {
                    entities.add(targetEntities.get(id));
                }
            });
            results.put(key, entities);
        });
        return results;
    }


    private void fillManyToOneFields(List<?> domains, QueryField field, JoinRelation join) {
        String column = join.getJoins()[0];
        Set<Serializable> ids = domains.stream().map(domain -> (Serializable) ObjectUtils.getProperty(domain, join.getJoins()[0])).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Serializable, ? extends BaseDomain> targetDomains = findManyToOneEntities(join, field, ids);
            domains.forEach(domain -> {
                Serializable id = (Serializable) ObjectUtils.getProperty(domain, column);
                try {
                    BaseDomain target = targetDomains.get(id);
                    if (target != null) {
                        ObjectUtils.setProperty(domain, field.getName(), target);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    private void fillOneToManyFields(List<? extends BaseDomain> entities, QueryField field, JoinRelation join) {
        Set<Serializable> ids = entities.stream().map(BaseDomain::tid).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Serializable, List<? extends BaseDomain>> refEntities = findOneToManyEntities(join, field, ids);
            entities.forEach(entity -> {
                Serializable id = entity.tid();
                try {
                    List<?> refs = refEntities.get(id);
                    ObjectUtils.setProperty(entity, field.getName(), refs);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    private void fillManyToManyFields(List<? extends BaseDomain> entities, QueryField field, JoinRelation join) {
        Set<Serializable> ids = entities.stream().map(BaseDomain::tid).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() > 0) {
            Map<Serializable, List<? extends BaseDomain>> refEntities = findManyToManyEntities(join, field, ids);
            entities.forEach(entity -> {
                Serializable id = entity.tid(); // (Long)BeanUtils.getProperty(entity, join.joinColumns()[0]);
                try {
                    List<?> refs = refEntities.get(id);
                    ObjectUtils.setProperty(entity, field.getName(), refs);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }


    public void fillJoinRelations(QueryRequest queryRequest, List<T> entities, Class<?> clz) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        Map<String, JoinRelation> joins = classCache.getJoins(clz);
        if (CollectionUtils.isNotEmpty(queryRequest.getFields()) && queryRequest.getFields().size() == 1 &&
        queryRequest.getFields().get(0).getName().equals("*")) {
            joins.forEach((field, join) -> fillJoinRelations(entities, new SimpleField(field), join));
        } else {
            if(CollectionUtils.isNotEmpty(queryRequest.getFields())) {
                for (QueryField field : queryRequest.getFields()) {
                    if (joins.containsKey(field.getName())) {
                        JoinRelation join = joins.get(field.getName());
                        fillJoinRelations(entities, field, join);
                    }
                }
            }
        }
    }


    public void fillJoinRelations(List<? extends BaseDomain> entities, QueryField field, JoinRelation join) {
        if (join.getRelationType().equals(RelationType.ManyToOne) || join.getRelationType().equals(RelationType.OneToOne)) {
            fillManyToOneFields(entities, field, join);
        } else if (join.getRelationType().equals(RelationType.ManyToMany)) {
            fillManyToManyFields(entities, field, join);
        } else if (join.getRelationType().equals(RelationType.OneToMany)) {
            fillOneToManyFields(entities, field, join);
        }
    }


    public void fillJoinRelations(List<T> entities, Class<?> clz) {
        Map<String, JoinRelation> joins = classCache.getJoins(clz);
        if (joins != null) {
            joins.forEach((fieldName, join) -> {
                SimpleField simpleField = new SimpleField(fieldName);
                fillJoinRelations(entities, simpleField, join);
            });
        }
    }

    public BaseQuickService<T> findService(Class<?> clz) {
        return serviceRegistry.findService(clz.getSimpleName());
    }


}
