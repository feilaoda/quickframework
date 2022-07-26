package com.quickpaas.framework.quickql.argument;

import com.quickpaas.framework.quickql.enums.FilterOp;
import lombok.Data;

@Data
public class ArgumentValue extends Argument{
    private FilterOp op;
    private Object value;
}
