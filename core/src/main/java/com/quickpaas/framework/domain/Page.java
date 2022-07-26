package com.quickpaas.framework.domain;


import lombok.Data;

@Data
public class Page {
    private long pages; //总页数

    private long total;//总条数

    private long current; //当前页

    private long size;  //当前页条数

}