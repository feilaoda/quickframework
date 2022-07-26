package com.quickpaas.framework.quickql;

import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.json.JsonObject;
import com.quickpaas.framework.quickql.argument.*;
import com.quickpaas.framework.quickql.enums.AndOr;
import com.quickpaas.framework.quickql.enums.FilterOp;
import com.quickpaas.framework.quickql.field.ObjectField;
import com.quickpaas.framework.quickql.field.QueryOpField;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.field.SimpleField;
import com.quickpaas.framework.quickql.filter.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Data
public class QueryParser {

    public final static String SPECLICAL_DOT = "@";
    public final static String AND_OP = "and";
    public final static String OR_OP = "or";
    private final static long MaxPageSize = 100000L;
//    @Autowired
    private ClassCache classCache;

    public QueryParser(ClassCache classCache) {
        this.classCache = classCache;
    }
    public QueryRequest parseQueryRequest(String json) {
        return parseQueryRequest(null, json);
    }
    public QueryRequest parseQueryRequest(Class<?> clz, String json) {
        QueryRequest query = new QueryRequest();
        if (StringUtils.isEmpty(json)) {
            json = "{}";
        }
        JsonObject jsonObject = JsonObject.parseObject(json);
        query.setVersion(jsonObject.getInteger("v"));
        String name = jsonObject.getString("name");
        query.setName(name);
        if(clz == null) {
            clz = classCache.findClass(name);
        }
        query.setClz((Class)clz);
        Object argsObject = jsonObject.getMap("filter");
        if (argsObject != null && argsObject instanceof Map) {
            Map<String, Object> argsMap = (Map) argsObject;
            List<Argument> args = parseQueryArguments(argsMap);
            List<QueryFilter> fields = parseArgsToFields(clz, args);
            query.setFilters(fields);
        } else {
            query.setFilters(new ArrayList<>());
        }
        Object outObject = jsonObject.getMap("query");
        if (outObject != null && outObject instanceof Map) {
            Map<String, Object> map = (Map)outObject;
            List<QueryField> fields = parseQueryFields(map);
            query.setFields(fields);
        } else {
            query.setFields(new ArrayList<>());
        }

        query.setOrderBy(new ArrayList<>());
        JsonObject pageObj = jsonObject.getJsonObject("page");
        if (pageObj != null) {
            QueryPage page = new QueryPage();
            Long current = pageObj.getLong("current");
            Long pageSize = pageObj.getLong("pageSize");
            if (current != null) {
                page.setCurrent(current);
            }
            if (pageSize != null) {
                page.setPageSize(pageSize);
            }
            if (MaxPageSize < page.getPageSize()) {
                page.setPageSize(MaxPageSize);
            }
            query.setPage(page);
        }
        return query;
    }


    public QueryRequest parseQueryRequest(Query query, Class<?> clz) {
        QueryRequest request = new QueryRequest();
        request.setVersion(query.getVersion());
        request.setName(query.getName());
        if(clz == null) {
            clz = classCache.findClass(query.getName());
        }
        request.setClz((Class)clz);
        Map<String, FilterValue> filtersMap = query.getWheres();
        if (filtersMap != null) {
            Map<String, Object> argsMap = new HashMap<>();
            filtersMap.forEach((key, value) -> {
                Map<String, Object> values = new HashMap<>();
                values.put(SPECLICAL_DOT+value.getOp().name(), value.getValue());
                argsMap.put(key, values);
            });
            List<Argument> args = parseQueryArguments(argsMap);
            List<QueryFilter> fields = parseArgsToFields(clz, args);
            request.setFilters(fields);
        } else {
            request.setFilters(new ArrayList<>());
        }
        Map<String, Object> queryMap = query.getFields();
        if (queryMap != null) {
            List<QueryField> fields = parseQueryFields(queryMap);
            request.setFields(fields);
        } else {
            request.setFields(new ArrayList<>());
        }

        request.setOrderBy(new ArrayList<>());
        QueryPage queryPage = query.getPage();
        request.setPage(queryPage);
        return request;
    }

    private List<QueryField> parseQueryFields(Map<String, Object> map) {
        List<QueryField> fields = new ArrayList<>();
        for(Map.Entry<String, Object> entry: map.entrySet()) {
            if(entry.getKey().startsWith(SPECLICAL_DOT)) {
                String key = entry.getKey().substring(1);
                fields.add(new QueryOpField(key, entry.getValue()));
            }else {
                Object value = entry.getValue();
                if (value instanceof String || value instanceof Boolean) {
                    SimpleField field = new SimpleField(entry.getKey());
                    fields.add(field);
                } else if (value instanceof Map) {
                    List<QueryField> subFields = parseQueryFields((Map) value);
                    ObjectField field = new ObjectField(entry.getKey());
                    field.setFields(subFields);
                    fields.add(field);
                }
            }
        }
        return fields;
    }


    public List<Argument> parseQueryArguments(Map<String, Object> argsMap) {
        List<Argument> args = new ArrayList<>();
        for (Map.Entry<String, Object> entry : argsMap.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (key.startsWith(SPECLICAL_DOT)) {
                String op = key.substring(1).toUpperCase();
                if (op.equals(AND_OP) || op.equals(OR_OP)) {
                    AndOrArgument andOrArgument = new AndOrArgument();
                    andOrArgument.setOp(AndOr.parse(op));
                    if (val instanceof Map) {
                        List<Argument> listArgs = parseQueryArguments((Map) val);
                        andOrArgument.addAll(listArgs);
                    }
                    args.add(andOrArgument);
                } else {
                    OpValue argValue = parseQueryOpValue(op, val);
                    args.add(argValue);
                }
            } else {
                if (val instanceof String || val instanceof Number) {
                    ArgumentValue opValue = new ArgumentValue();
                    opValue.setOp(FilterOp.EQ);
                    opValue.setValue(val);
                    opValue.setName(key);
                    args.add(opValue);
                } else if (val instanceof List) {
                    ArgumentValue opValue = new ArgumentValue();
                    opValue.setOp(FilterOp.EQ);
                    opValue.setValue(val);
                    opValue.setName(key);
                    args.add(opValue);
                } else if (val instanceof Map) {
                    List<Argument> subArgs = parseQueryArguments((Map) val);
                    if (CollectionUtils.isNotEmpty(subArgs)) {
                        if (subArgs.size() > 1) {
                            ListArgument listArg = new ListArgument();
                            for(Argument a: subArgs) {
                                if(a instanceof OpValue) {
                                    ArgumentValue argValue = new ArgumentValue();
                                    argValue.setName(key);
                                    argValue.setOp(((OpValue) a).getOp());
                                    argValue.setValue(((OpValue) a).getValue());
                                    listArg.add(argValue);
                                }
                            }
//                            listArg.addAll(subArgs);
                            listArg.setName(key);
                            args.add(listArg);
                        } else {
                            Argument first = subArgs.get(0);
//                            SimpleArgument arg = new SimpleArgument();
//                            arg.setName(key);
//                            arg.setArgument(first);
//                            args.add(arg);

                            if( first instanceof OpValue) {
                                ArgumentValue arg = new ArgumentValue();
                                arg.setName(key);
                                arg.setOp(((OpValue) first).getOp());
                                arg.setValue(((OpValue) first).getValue());
                                args.add(arg);
                            }else {
                                SimpleArgument arg = new SimpleArgument();
                                arg.setName(key);
                                arg.setArgument(first);
                                args.add(arg);
                            }
                        }
                    }

                }
            }

        }
        return args;
    }

    private OpValue parseQueryOpValue(String opStr, Object value) {
        OpValue opValue = new OpValue();
        opValue.setName(opStr);
        if (value instanceof String || value instanceof Number || value instanceof List) {
            FilterOp op = FilterOp.valueOf(opStr);
            opValue.setOp(op);
            opValue.setValue(value);
        } else if (value instanceof Map) {
            //error
        }

        return opValue;
    }

    private ArgumentValue parseQueryOpValue(Map<String, Object> argsMap) {
        for (Map.Entry<String, Object> entry : argsMap.entrySet()) {
            ArgumentValue opValue = new ArgumentValue();
            opValue.setOp(FilterOp.valueOf(entry.getKey()));
            Object val = entry.getValue();
            if (val instanceof String || val instanceof Number || val instanceof List) {
                opValue.setValue(val);
                return opValue;
            }
        }
        return null;
    }

    private List<QueryFilter> parseArgsToFields(Class<?> clz, List<Argument> args) {
        Map<String, Field> classFields = classCache.getFields(clz);
        Map<String, JoinRelation> classJoins = classCache.getJoins(clz);

        List<QueryFilter> filters = new ArrayList<>();
        for (Argument arg : args) {
            if(arg instanceof AndOrArgument) {
                AndOrQueryFilter andOrFilter = new AndOrQueryFilter();
                andOrFilter.setOp(((AndOrArgument) arg).getOp());
                List<QueryFilter> subFilters = parseArgsToFields(clz, ((AndOrArgument)arg).getArguments());
                andOrFilter.setFilters(subFilters);
                filters.add(andOrFilter);
                continue;
            }

            NamePath namePath = NamePath.createNamePath(arg.getName());
            if (!classFields.containsKey(namePath.getName())) {
                log.warn("{} Filed {} is not found!", clz.getName(), namePath.getName());
                continue;
            }

            if (classJoins.containsKey(namePath.getName()) && namePath.hasNext()) {
                //引用
                Optional<QueryFilter> opt = filters.stream().filter(field -> field.getName().equals(namePath.getName())).findFirst();
                QueryFilter relField = opt.orElse(new RelationQueryFilter(namePath.getName()));

                if (!(relField instanceof RelationQueryFilter)) {
                    //error
                    continue;
                }
                RelationQueryFilter refFilter = (RelationQueryFilter) relField;
                JoinRelation join = classJoins.get(namePath.getName());
                refFilter.setJoinRelation(join);
                if (arg instanceof SimpleArgument) {
                    SimpleArgument queryArg = (SimpleArgument) arg;
                    QueryFilter valueField = parseQueryFilter(join.getTarget(), refFilter.getFilters(), namePath.getNext(), queryArg.getArgument());
                    if (valueField != null) {
                        refFilter.add(valueField);
                    }

                }else if(arg instanceof ArgumentValue) {

                    QueryFilter valueFilter = parseQueryFilter(join.getTarget(), refFilter.getFilters(), namePath.getNext(), arg);
                    if(valueFilter != null) {
                        refFilter.add(valueFilter);
                    }

                }

                if (opt.isPresent()) {
                    filters.add(refFilter);
                }
            } else {
                if(classJoins.containsKey(namePath.getName())) {
                    //关联字段
                    Optional<QueryFilter> opt = filters.stream().filter(field -> namePath.getName().equals(field.getName())).findFirst();
                    QueryFilter relField = opt.orElse(new RelationQueryFilter(namePath.getName()));
                    if (!(relField instanceof RelationQueryFilter)) {
                        //error
                        continue;
                    }
                    RelationQueryFilter refFilter = (RelationQueryFilter) relField;
                    JoinRelation join = classJoins.get(namePath.getName());
                    refFilter.setJoinRelation(join);

                    if(arg instanceof ListArgument) {
                        List<QueryFilter> queryFilters = parseArgsToFields(join.getTarget(), ((ListArgument) arg).getArguments());
                        if(queryFilters.size()>0) {
                            refFilter.addAll(queryFilters);
                        }
                    }else if(arg instanceof SimpleArgument) {
                        List<QueryFilter> queryFilters = parseArgsToFields(join.getTarget(), Collections.singletonList(((SimpleArgument) arg).getArgument()));
                        if(queryFilters.size()>0) {
                            refFilter.addAll(queryFilters);
                        }
                    }else if(arg instanceof ArgumentValue) {
                        List<QueryFilter> queryFilters = parseArgsToFields(join.getTarget(), Collections.singletonList(arg));
                        if(queryFilters.size()>0) {
                            refFilter.addAll(queryFilters);
                        }
                    }else{
                        //error
                        log.error("关联字段筛选Error, {}", namePath.getNameWithParent());
                        throw new QuickException("关联字段筛选Error, {}", namePath.getName());
                    }

//                    if (arg instanceof SimpleArgument) {
//                        SimpleArgument queryArg = (SimpleArgument) arg;
//
//                        QueryFilter valueField = parseQueryFilter(join.getTarget(), refFilter.getFilters(), namePath.getNext(), queryArg.getArgument());
//                        if (valueField != null) {
//                            refFilter.add(valueField);
//                        }
//                    }

                    if (!opt.isPresent()) {
                        filters.add(refFilter);
                    }
                }else {
                    //普通字段
                    if (arg instanceof ArgumentValue) {
//                        SimpleArgument simpleArgument = (SimpleArgument) arg;
//                        if (!(simpleArgument.getArgument() instanceof ArgumentValue)) {
//                            //error
//                            log.info("字段值不是普通值");
//                            continue;
//                        }
                        ArgumentValue argValue = (ArgumentValue) arg;
                        Field objectField = classFields.get(namePath.getName());
                            List<SimpleValueQueryFilter> fs = createFilters(objectField, namePath, argValue);
                            filters.addAll(fs);
                    } else if(arg instanceof ListArgument) {
                        for(Argument argument: ((ListArgument) arg).getArguments()) {
                            if(argument instanceof ArgumentValue) {
                                ArgumentValue argValue = (ArgumentValue) argument;
                                Field objectField = classFields.get(namePath.getName());
                                List<SimpleValueQueryFilter> fs = createFilters(objectField, namePath, argValue);
                                filters.addAll(fs);
                            }
                        }
                    }
                    else if(arg instanceof SimpleArgument) {
                        log.error("这里怎么会有SimpleArgument? {}", arg);
                    }
                }
            }
        }

        return filters;
    }

    private QueryFilter parseQueryFilter(Class<?> clz, List<QueryFilter> fields, NamePath namePath, Argument value) {
        Map<String, Field> classFields = classCache.getFields(clz);
        Map<String, JoinRelation> classJoins = classCache.getJoins(clz);

        if (namePath.hasNext()) {
            //还有引用
            Optional<QueryFilter> opt = fields.stream().filter(field -> field.getName().equals(namePath.getName())).findFirst();
            RelationQueryFilter relField = (RelationQueryFilter) opt.orElse(new RelationQueryFilter(namePath.getName()));

//            RelationQueryFilter field = new RelationQueryFilter(namePath.getName());
            if (!classJoins.containsKey(namePath.getName())) {
                //error
            }
            JoinRelation join = classJoins.get(namePath.getName());
            relField.setJoinRelation(join);
            QueryFilter queryField = parseQueryFilter(join.getTarget(), relField.getFilters(), namePath.getNext(), value);
            relField.add(queryField);
            if (!opt.isPresent()) {
                fields.add(relField);
            }
            return null;
        } else {
            if (!classFields.containsKey(namePath.getName())) {
                //error
                log.error("未知的字段, {}, {}, {}", clz.getName(), namePath.getName(), namePath.getNameWithParent());
                throw new QuickException("未知的字段 " + namePath.getNameWithParent());
            }
            if (classJoins.containsKey(namePath.getName())) {
                //error,引用字段不能直接作为条件
                log.error("引用字段不能直接作为条件, {}, {}", clz.getName(), namePath.getNameWithParent());
//                return null;
            }
            Field objectField = classFields.get(namePath.getName());
            SimpleValueQueryFilter field = createFilter(objectField, namePath, value);
            return field;
        }
    }

    private SimpleValueQueryFilter createFilter(Field field, NamePath namePath, Argument value) {
        SimpleValueQueryFilter simpleField = new SimpleValueQueryFilter();
        simpleField.setName(namePath.getName());
        if (value instanceof ArgumentValue) {
            ArgumentValue argValue = (ArgumentValue) value;
            simpleField.setOp(argValue.getOp());
            simpleField.setValue(argValue.getValue());
        }
        simpleField.setFieldType(field.getType());
        return simpleField;
    }

    private List<SimpleValueQueryFilter> createFilters(Field field, NamePath namePath, ArgumentValue value) {
        List<SimpleValueQueryFilter> filters = new ArrayList<>();
        if(value.getValue() instanceof List) {
            if(FilterOp.IN.equals(value.getOp())) {
                SimpleValueQueryFilter simpleField = new SimpleValueQueryFilter();
                simpleField.setName(namePath.getName());
                simpleField.setOp(value.getOp());
                simpleField.setValue(value.getValue());
                simpleField.setFieldType(field.getType());
                filters.add(simpleField);
            }else {
                for (Object val : (List) value.getValue()) {
                    SimpleValueQueryFilter simpleField = new SimpleValueQueryFilter();
                    simpleField.setName(namePath.getName());
                    simpleField.setOp(value.getOp());
                    simpleField.setValue(val);
                    simpleField.setFieldType(field.getType());
                    filters.add(simpleField);
                }
            }
        }else {
            SimpleValueQueryFilter simpleField = new SimpleValueQueryFilter();
            simpleField.setName(namePath.getName());
            simpleField.setOp(value.getOp());
            simpleField.setValue(value.getValue());
            simpleField.setFieldType(field.getType());
            filters.add(simpleField);
        }

        return filters;
    }

}
