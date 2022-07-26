package com.quickpaas.framework.quickql.enums;

public enum QueryOp {
    LIMIT("limit"),
    OFFSET("offset"),
    ORDERBY("order by"),
    ORDERBYDESC("order by desc"),
    GROUPBY("group by");

    private String dbOp;
    QueryOp(String op) {
        this.dbOp = op;
    }

    public String getDbOp() {
        return dbOp;
    }
}
