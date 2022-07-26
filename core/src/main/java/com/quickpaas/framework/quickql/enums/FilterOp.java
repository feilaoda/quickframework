package com.quickpaas.framework.quickql.enums;

public enum FilterOp {
    EQ("="),GT(">"),GE(">="),LE("<="),NE("<>"),
    LT("<"),LIKE("like"),
    IN("in"),NOTNULL("is not null"),
    ISNULL("is null"),
    RANGE("range"),
    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max");

    private String dbOp;
    FilterOp(String op) {
        this.dbOp = op;
    }

    public String getDbOp() {
        return dbOp;
    }
}
