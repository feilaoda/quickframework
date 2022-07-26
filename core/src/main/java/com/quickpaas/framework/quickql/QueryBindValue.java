package com.quickpaas.framework.quickql;

import lombok.Data;

@Data
public class QueryBindValue {
    private byte[] bytesValue;
    private FieldType type;
}
