package com.quickpaas.framework.domain;


import lombok.Data;

import java.util.List;

@Data
public class Page<T> {
    private long pages; //总页数

    private long total;//总条数

    private long current; //当前页

    private long size;  //当前页条数

    private List<T> records;

    public Page() {
        this(1,0,0);
    }

    public Page(long current, long size) {
        this(current, size, 0);
    }
    public Page(long current, long size, long total) {
        this.current = current;
        this.size = size;
        this.total = total;
        if(size == 0) {
            this.pages = 0;
        }else {
            this.pages = this.getTotal() / this.getSize();
            if (this.getTotal() % this.getSize() != 0L) {
                ++this.pages;
            }
        }
    }
}