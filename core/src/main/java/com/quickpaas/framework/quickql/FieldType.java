package com.quickpaas.framework.quickql;

public enum FieldType {
    STRING(0),
    INTEGER(1),
    LONG(2);
    int type;
    FieldType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
