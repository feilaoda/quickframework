package com.quickpaas.framework.quickql;

import lombok.Data;

@Data
public class QueryPage {
    private Long current = 1L;
    private Long pageSize = 10L;
    private Long total = 0L;//总条数

    public Long getOffset() {
        return (current-1)*pageSize;
    }
}
