package com.quickpaas.framework.domain;

import lombok.Data;

@Data
public class JoinRelation {
    private RelationType relationType;
    private String[] joins;
    private Class<?> target;
    private Class<?> middle;
}
