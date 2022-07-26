package com.quickpaas.framework.quickql.argument;

import lombok.Data;

@Data
public class SimpleArgument extends Argument{
    private String name;
//    private ArgumentValue value;
    private Argument argument;
}
