package com.quickpaas.framework.quickql.filter;

import lombok.Data;

@Data
public class QueryFilter {
    private String name;

    public QueryFilter() {
    }

    public QueryFilter(String name) {
        this.name = name;
    }
}
