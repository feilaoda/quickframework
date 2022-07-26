package com.quickpaas.framework.quickql;

import lombok.Data;

@Data
public class NamePath {
    private String name;
    private NamePath parent;
    private NamePath next;

    private NamePath(String name) {
        this.name = name;
    }

    public String getNameWithParent() {
        StringBuilder sb = new StringBuilder();
        if(parent != null) {
            sb.append(parent.getNameWithParent());
        }else {
        }
        if(sb.length() > 0) {
            sb.append(".");
        }
        sb.append(name);

        return sb.toString();
    }

    public String toString() {
        return name;
    }

    public boolean hasNext() {
        return next != null;
    }

    public static NamePath createNamePath(String name) {

        if (name.contains(".")) {
            String[] arr = name.split("\\.");
            NamePath namePath = new NamePath(arr[0]);
            if (arr.length > 1) {
                NamePath first = namePath;
                for (int i=1; i<arr.length; i++) {
                    NamePath next = new NamePath(arr[i]);
                    first.next = next;
                    next.parent = first;
                    first = next;
                }
            }
            return namePath;
        }else {
            return new NamePath(name);
        }
    }


}
