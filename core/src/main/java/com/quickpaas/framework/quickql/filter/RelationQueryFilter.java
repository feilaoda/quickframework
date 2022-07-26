package com.quickpaas.framework.quickql.filter;

import com.quickpaas.framework.domain.JoinRelation;
import com.quickpaas.framework.domain.RelationType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RelationQueryFilter extends QueryFilter{
    private RelationType relationType;
    private JoinRelation joinRelation;
    private List<QueryFilter> filters;

    public RelationQueryFilter(String name) {
        super(name);
        filters = new ArrayList<>();
    }

    public void add(QueryFilter field) {
        filters.add(field);
    }

    public void addAll(List<QueryFilter> filterList) {
        filters.addAll(filterList);
    }
}
