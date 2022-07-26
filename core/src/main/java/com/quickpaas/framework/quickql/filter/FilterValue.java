package com.quickpaas.framework.quickql.filter;

import com.quickpaas.framework.quickql.enums.FilterOp;
import lombok.Data;

@Data
public class FilterValue {
    private FilterOp op;
    private Object value;
}
