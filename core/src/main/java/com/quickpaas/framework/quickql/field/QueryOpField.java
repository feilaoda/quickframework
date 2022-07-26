package com.quickpaas.framework.quickql.field;

import com.quickpaas.framework.quickql.enums.QueryOp;
import lombok.Data;

@Data
public class QueryOpField extends QueryField{
    private Object value;
    private QueryOp op;
    public QueryOpField(String name, Object value) {
        super(name);
        op = QueryOp.valueOf(name.toUpperCase());
        this.value = value;
    }
}
