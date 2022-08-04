package com.quickpaas.framework.quickql.filter;

import com.quickpaas.framework.quickql.enums.FilterOp;
import lombok.Data;

@Data
public class SimpleValueQueryFilter extends QueryFilter{
    private String name;
    private Class fieldType;
    private Class valueType;
    private FilterOp op;
    private Object value;
}
