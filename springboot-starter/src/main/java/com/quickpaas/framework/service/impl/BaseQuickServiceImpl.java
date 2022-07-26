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
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.quickql.*;
import com.quickpaas.framework.quickql.enums.AndOr;
import com.quickpaas.framework.quickql.enums.FilterOp;
import com.quickpaas.framework.quickql.enums.QueryOp;
import com.quickpaas.framework.quickql.field.ObjectField;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.field.QueryOpField;
import com.quickpaas.framework.quickql.filter.AndOrQueryFilter;
import com.quickpaas.framework.quickql.filter.QueryFilter;
import com.quickpaas.framework.quickql.filter.RelationQueryFilter;
import com.quickpaas.framework.quickql.filter.SimpleValueQueryFilter;
import com.quickpaas.framework.service.BaseService;
import com.quickpaas.framework.service.QueryRepository;
import com.quickpaas.framework.service.QuickService;
import com.quickpaas.framework.utils.NameUtils;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.framework.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseQuickServiceImpl<M extends BaseMapper<E>, E extends BaseEntity, D extends BaseDomain<E>> extends ServiceImpl<M, E> implements QuickService<E, D>, IService<E> {

    @Autowired
    private ClassCache classCache;

    @Autowired
    private QueryRepository<E, D> queryRepository;

    private Class<E> entityClass;
    private Class<D> domainClass;

    public BaseQuickServiceImpl() {
        entityClass = (Class<E>) ReflectionUtils.getSuperClassGenericType(this.getClass(), 1);
        domainClass = (Class<D>) ReflectionUtils.getSuperClassGenericType(this.getClass(), 2);
    }


    public E selectOne(QueryRequest request) {
        if(request == null) {
            return null;
        }
        QueryWrapper<E> wrapper = createQuery(request);
        E data = baseMapper.selectOne(wrapper);
        return data;
    }

    public List<E> selectList(QueryRequest request) {
        if(request == null) {
            return new ArrayList<>();
        }
        QueryWrapper<E> wrapper = createQuery(request);
        List<E> data = list(wrapper);
        return data;
    }

    @Override
    public void fillJoinRelations(List<D> entities, QueryField queryField, JoinRelation join) {
        queryRepository.fillJoinRelations(entities, queryField, join);
    }

    public void fillJoinRelations(QueryRequest request, List<D> entities, Class clz) {
        queryRepository.fillJoinRelations(request, entities, clz);
    }



    @Override
    public List<E> selectEntitiesByIds(List<String> selectColumns, String idsColumn, Collection<Long> ids) {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        if(CollectionUtils.isNotEmpty(selectColumns)) {
            queryWrapper.select(selectColumns.toArray(new String[]{}));
        }
        queryWrapper.in(idsColumn, ids);
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    public List<E> selectEntitiesByIds(QueryField field, String idsColumn, Collection<Long> ids) {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        if(field instanceof ObjectField) {
            List<QueryOpField> opFields = ((ObjectField) field).getFields().stream().filter(e-> e instanceof QueryOpField).map(e->(QueryOpField)e).collect(Collectors.toList());
            for(QueryOpField opField: opFields) {
                int limit = 100000;
                int offset = 0;
                boolean hasLimit = false;
//                if(QueryOp.LIMIT.equals(opField.getOp())) {
//                    limit = Integer.valueOf(opField.getValue().toString());
//                    hasLimit = true;
//                }else if(QueryOp.OFFSET.equals(opField.getOp())) {
//                    offset = Integer.valueOf(opField.getValue().toString());
//                    hasLimit = true;
//                }else
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


    public QueryWrapper<E> createQuery(QueryRequest request) {
        QueryWrapper<E> wrapper = new QueryWrapper<>();
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


    private QueryWrapper<E> fillQueryWrapper(Class<?> targetClass,  List<QueryFilter> filters, List<String> columns) {
        QueryWrapper<E> wrapper = new QueryWrapper<>();
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

    private void fillQueryWrapper(Class<?> targetClass, QueryWrapper<E> wrapper, List<QueryFilter> filters) {
        for (QueryFilter queryFilter : filters) {
            fillQueryWrapper(targetClass, wrapper, queryFilter);
        }
    }
    private void fillQueryWrapper(Class<?> targetClass, QueryWrapper<E> wrapper, QueryFilter queryFilter) {
        if (queryFilter instanceof AndOrQueryFilter) {
            if (((AndOrQueryFilter) queryFilter).getOp().equals(AndOr.AND)) {
                fillQueryWrapper(targetClass, wrapper, ((AndOrQueryFilter) queryFilter).getFilters());
            } else {
                wrapper.and(true, new Consumer<QueryWrapper<E>>() {
                            public void accept(QueryWrapper<E> queryWrapper) {
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


    public void fillQueryWrapper(QueryWrapper<?> wrapper, FilterOp op, String name, Object value) {
        boolean condition = false;
        if(value instanceof String) {
            condition = StringUtils.isNotEmpty(value.toString());
        }else {
            condition = value != null;
        }
        String columnName = NameUtils.toColumnName(name);

        if (op == null) {
            op = FilterOp.EQ;
        }
        if (op.equals(FilterOp.EQ)) {
            wrapper.eq(condition, columnName, value);
        } else if (op.equals(FilterOp.LIKE)) {
            wrapper.like(condition, columnName, value);
        } else if (op.equals(FilterOp.IN) && value instanceof List) {
            wrapper.in(columnName, value);
        } else if (op.equals(FilterOp.ISNULL)) {
            wrapper.isNull(columnName);
        } else if (op.equals(FilterOp.NOTNULL)) {
            wrapper.isNotNull(columnName);
        } else if (op.equals(FilterOp.GT)) {
            wrapper.gt(condition, columnName, value);
        }else if (op.equals(FilterOp.LT)) {
            wrapper.lt(condition, columnName, value);
        }else if (op.equals(FilterOp.LE)) {
            wrapper.le(condition, columnName, value);
        }else if (op.equals(FilterOp.GE)) {
            wrapper.ge(condition, columnName, value);
        }else {
            throw new QuickException("unknown filter op: " + op);
        }
    }

    private void fillSimpleQueryWrapper(Class<?> targetClass, QueryWrapper<E> wrapper, SimpleValueQueryFilter queryFilter) {//} QueryOp op, String name, Object value) {
        fillOpQueryWrapper(targetClass, wrapper, queryFilter.getOp(), queryFilter.getName(), queryFilter.getValue());
    }

    private void fillOpQueryWrapper(Class<?> targetClass, QueryWrapper<E> wrapper, FilterOp op, String fieldName, Object value) {
        boolean condition = false;

        condition = value != null && !StringUtils.isEmpty(value.toString());
        String columnName = classCache.getColumnName(targetClass, fieldName);

        if (op == null) {
            op = FilterOp.EQ;
        }
        if (op.equals(FilterOp.EQ)) {
            wrapper.eq(condition, columnName, value);
        } else if (op.equals(FilterOp.LIKE)) {
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
        }
    }

    private void fillRelationQueryWrapper(QueryWrapper<E> wrapper, RelationQueryFilter relationFilter) {
        JoinRelation relation = relationFilter.getJoinRelation();
        if (relation.getRelationType().equals(RelationType.ManyToOne)) {
            Class<?> clz = relation.getTarget();
//            if (relationFilter.getFilters().size() == 1 && relationFilter.getFilters().get(0).getName().equals("id")
//                    && relationFilter.getFilters().get(0) instanceof SimpleQueryFilter) {
//                //id优化
//                QueryFilter filter = relationFilter.getFilters().get(0);
//                if (filter instanceof SimpleQueryFilter) {
//                    SimpleQueryFilter simpleField = (SimpleQueryFilter) filter;
//                    Object val = simpleField.getValue();
//                    String column = relation.getJoins()[0];
//                    wrapperQueryOp(wrapper, simpleField.getOp(), column, val);
//                    return;
//                } else {
//                    //error
//                }
//            }
            List<String> cols = Lists.newArrayList("id");
            List<BaseEntity> entities = (List<BaseEntity>) queryEntityList(clz, relationFilter.getFilters(), cols);
            List<Long> ids = entities.stream().map(BaseEntity::getId).collect(Collectors.toList());
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
            List<BaseEntity> entities = (List<BaseEntity>) queryEntityList(clz, relationFilter.getFilters(), cols);
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
        if (queryFilter.getFilters().size() == 1 && fields.size() == 1 && "id".equals(fields.get(0).getName())) {
            String sqlFmt = "SELECT %s.%s FROM %s %s WHERE  ";
            String selectSql = String.format(sqlFmt,
                    leftAlias,
                    NameUtils.toColumnName(leftColumn),
                    middleTable.value(),
                    leftAlias
            );
            sb.append(selectSql);
            SimpleValueQueryFilter first = (SimpleValueQueryFilter) fields.get(0);
            String where = leftAlias + "." + NameUtils.toColumnName(rightColumn) + " " + first.getOp().getDbOp();
            String v = first.getValue().toString();
            if (StringUtils.isNotEmpty(v)) {
                where += " " + v;
            }
            sb.append(where);

        } else {
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
        }

        return sb.toString();
    }
    private String convertSqlOp(FilterOp op) {
        return op.getDbOp();
    }

    private void setValue(QueryBindValue[] values, int index, SimpleValueQueryFilter field) {
        if (field.getFieldType().equals(String.class)) {
            setString(values, index, field.getValue().toString());
        } else if (field.getFieldType().equals(Integer.class)) {
            setValue(values, index, String.valueOf(Integer.valueOf(field.getValue().toString())), FieldType.INTEGER);
        } else if (field.getFieldType().equals(Long.class)) {
            setValue(values, index, String.valueOf(Long.valueOf(field.getValue().toString())), FieldType.LONG);
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

    public static byte[] getBytes(String value) {
        return value.getBytes();
    }


    public List<? extends BaseEntity> queryEntityList(Class<?> domainClazz, List<QueryFilter> filters, List<String> columns) {
        QuickService<?,?> service = queryRepository.findService(domainClazz);
        return service.selectList(filters, columns);
    }

    @Override
    public List<E> selectList(List<QueryFilter> filters, List<String> columns) {
        QueryWrapper<E> wrapper = fillQueryWrapper(entityClass, filters, columns);
        return list(wrapper);
    }

    @Override
    public String getColumnName(Class<?> targetDomainClass, String column) {
        try {
            Field field = targetDomainClass.getDeclaredField(column);

            TableField tableField = field.getAnnotation(TableField.class);
            if(tableField == null) {
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
            }else {
                return tableField.value();
            }
        } catch (NoSuchFieldException e) {
            log.warn(e.getMessage(), e);
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column) ;
    }

    @Override
    public void removeByColumn(String column, Object value) {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        getBaseMapper().delete(queryWrapper);
    }

    @Override
    public void createOrSave(BaseEntity entity) {
        save((E)entity);
    }


}
