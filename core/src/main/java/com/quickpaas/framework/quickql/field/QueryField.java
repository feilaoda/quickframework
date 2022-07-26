package com.quickpaas.framework.quickql.field;

import lombok.Data;

@Data
public class QueryField {
    private String name;

    public QueryField() {
    }

    public QueryField(String name) {
        this.name = name;
    }
}
