package com.quickpaas.framework.quickql;

import com.quickpaas.framework.json.JSON;
import com.quickpaas.framework.quickql.enums.FilterOp;
import com.quickpaas.framework.quickql.filter.FilterValue;
import lombok.Data;

import java.util.*;

@Data
public class Query {
    private Integer version;
    private String name;
    private Map<String, Object> where;
    private List<String> fields;
    private QueryPage page;
    private Class<?> clazz;

    public static  Query create(String json) {
        Query query = JSON.parseObject(json, Query.class);
        if(query.getWhere() == null) {
            query.setWhere(new HashMap<>());
        }
        if(query.getFields() == null) {
            query.setFields(new ArrayList<>());
        }
        return query;
    }


    public Query() {
        where = new HashMap<>();
        fields = new ArrayList<>();
    }

    public Query(String... names) {
        this();
        field(names);
    }

    public Query eq(String name,Object value) {
        addFilter(name, value);
        return this;
    }

    public Query gt(String name,Object value) {
        addFilter(name, FilterOp.GT, value);
        return this;
    }

    public Query ge(String name,Object value) {
        addFilter(name, FilterOp.GE, value);
        return this;
    }

    public Query lt(String name,Object value) {
        addFilter(name, FilterOp.LT, value);
        return this;
    }

    public Query le(String name,Object value) {
        addFilter(name, FilterOp.LE, value);
        return this;
    }

    public Query ne(String name,Object value) {
        addFilter(name, FilterOp.NE, value);
        return this;
    }


    public Query isNull(String name) {
        addFilter(name, FilterOp.ISNULL, "");
        return this;
    }

    public Query isNotNull(String name,Object value) {
        addFilter(name, FilterOp.NOTNULL, "");
        return this;
    }

    public Query in(String name, List<?> value) {
        addFilter(name, FilterOp.IN, value);
        return this;
    }

    public Query field(String name) {
        fields.add(name);
        return this;
    }

    public Query field(String ...names) {
        Arrays.stream(names).forEach(n -> fields.add(n));
        return this;
    }

    public void addFilter(String name, Object value) {
        addFilter(name, FilterOp.EQ, value);
    }
    public void addFilter(String name, FilterOp op, Object value) {
        FilterValue filter = new FilterValue();
        filter.setOp(op);
        filter.setValue(value);
        where.put(name, filter);
    }

    public Map<String, Object> fieldsToMap() {
        Map<String, Object> fieldsMap = new HashMap<>();
        for(String n: fields) {
            NamePath path = NamePath.createNamePath(n);
            addField(path, fieldsMap);
        }
        return fieldsMap;
    }

//    public void addField(Map<String, Object> fieldsMap, String ...name) {
//        for(String n: name) {
//            NamePath path = NamePath.createNamePath(n);
//            addField(path, fieldsMap);
//        }
//    }

    public void addField(NamePath namePath, Map<String, Object> fieldsMap) {
        if(namePath.hasNext()) {
            if(fieldsMap.containsKey(namePath.getName())) {
                Object value = fieldsMap.get(namePath.getName());
                if(value instanceof Map) {
                    addField(namePath.getNext(), (Map)value);
                }else {
                    Map<String, Object> map = new HashMap<>();
                    addField(namePath.getNext(), map);
                    fieldsMap.put(namePath.getName(), map);
                }
            }else {
                Map<String, Object> map = new HashMap<>();
                addField(namePath.getNext(), map);
                fieldsMap.put(namePath.getName(), map);
            }
        }else {
            if(!fieldsMap.containsKey(namePath.getName())) {
                fieldsMap.put(namePath.getName(), "");
            }

        }
    }

}
