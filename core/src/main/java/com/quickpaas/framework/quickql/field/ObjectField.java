package com.quickpaas.framework.quickql.field;

import com.quickpaas.framework.quickql.enums.QueryOp;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class ObjectField extends QueryField{
    private List<QueryField> fields;

    public ObjectField(String name) {
        super(name);
    }

    public Optional<QueryOpField>  filterField(QueryOp op) {
        return fields.stream().filter(e->e instanceof QueryOpField &&  op.equals(((QueryOpField)e).getOp())).map(e->(QueryOpField)e).findFirst();
    }
}
