package com.quickpaas.framework.quickql.argument;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListArgument extends Argument {
    private List<Argument> arguments;

    public ListArgument() {
        arguments = new ArrayList<>();
    }

    public void add(Argument argument) {
        arguments.add(argument);
    }
    public void addAll(List<Argument> args) {
        arguments.addAll(args);
    }
}
