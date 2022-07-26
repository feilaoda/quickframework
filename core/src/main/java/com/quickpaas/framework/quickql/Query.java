package com.quickpaas.framework.quickql;

import com.quickpaas.framework.json.JSON;
import com.quickpaas.framework.quickql.enums.FilterOp;
import com.quickpaas.framework.quickql.filter.FilterValue;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Query<T> {
    private Integer version;
    private String name;
    private Map<String, FilterValue> wheres;
    private Map<String, Object> fields;
    private QueryPage page;

    public static <T> Query<T> create(String json) {
        Query<T> query = JSON.parseObject(json, Query.class);
        if(query.getWheres() == null) {
            query.setWheres(new HashMap<>());
        }
        if(query.getFields() == null) {
            query.setFields(new HashMap<>());
        }
        return query;
    }


    public Query() {
        wheres = new HashMap<>();
        fields = new HashMap<>();
    }

    public Query(String... names) {
        wheres = new HashMap<>();
        fields = new HashMap<>();
        field(names);
    }

    public Query<T> eq(String name,Object value) {
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
        addField(name);
        return this;
    }

    public Query field(String ...name) {
        addField(name);
        return this;
    }

    public void addFilter(String name, Object value) {
        addFilter(name, FilterOp.EQ, value);
    }
    public void addFilter(String name, FilterOp op, Object value) {
//        Map<String, Object> filterValue = new HashMap<>();
//        filterValue.put(QueryParser.SPECLICAL_DOT + op.name(), value);
//        String columnName = name; //NameUtils.toColumnName(name);
        FilterValue filter = new FilterValue();
        filter.setOp(op);
        filter.setValue(value);
        wheres.put(name, filter);
    }

    public void addField(String ...name) {
        for(String n: name) {
            NamePath path = NamePath.createNamePath(n);
            addField(path, fields);
        }
    }

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
