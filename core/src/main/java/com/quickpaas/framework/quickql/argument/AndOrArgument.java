package com.quickpaas.framework.quickql.argument;

import com.quickpaas.framework.quickql.enums.AndOr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AndOrArgument extends SimpleArgument {
    private AndOr op;
    private List<Argument> arguments;

    public AndOrArgument() {
        arguments = new ArrayList<>();
    }

    public void addAll(List<Argument> args) {
        arguments.addAll(args);
    }
}
