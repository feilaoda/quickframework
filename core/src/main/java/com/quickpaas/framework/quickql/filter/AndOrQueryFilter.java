package com.quickpaas.framework.quickql.filter;

import com.quickpaas.framework.quickql.enums.AndOr;
import lombok.Data;

import java.util.List;

@Data
public class AndOrQueryFilter extends QueryFilter{
    private AndOr op;
    private List<QueryFilter> filters;
}
