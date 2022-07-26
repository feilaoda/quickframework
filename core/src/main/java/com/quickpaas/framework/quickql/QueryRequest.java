package com.quickpaas.framework.quickql;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.quickql.field.QueryField;
import com.quickpaas.framework.quickql.filter.QueryFilter;
import lombok.Data;

import java.util.List;

@Data
public class QueryRequest {
    private Class<? extends BaseDomain> clz;
    private String clazz;
    private String name;
    private List<QueryFilter> filters;
    private List<QueryField> fields;
    private List<String> orderBy;
    private QueryPage page;
    private boolean deepQuery;
    private Integer version;
}
