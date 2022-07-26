package com.quickpaas.framework.quickql.enums;

public enum AndOr {
    AND, OR;

    public static AndOr parse(String name) {
        return AndOr.valueOf(name.toUpperCase());
    }
}
