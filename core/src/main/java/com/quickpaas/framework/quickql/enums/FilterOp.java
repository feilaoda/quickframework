package com.quickpaas.framework.quickql.enums;

import com.quickpaas.framework.exception.QuickException;

public enum FilterOp {
    EQ("="),GT(">"),GE(">="),LE("<="),NE("<>"),
    LT("<"), CONTAINS("like"),
    IN("in"),NOTNULL("is not null"),
    ISNULL("is null"),
    RANGE("range"),
    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max");

    private String op;
    FilterOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public static FilterOp parse(String name) {
        try {
            return FilterOp.valueOf(name);
        }catch (IllegalArgumentException e) {
            throw new QuickException("未知的操作符："+name);
        }
    }
}
