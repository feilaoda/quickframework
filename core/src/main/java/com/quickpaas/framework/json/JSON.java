package com.quickpaas.framework.json;

public class JSON {
    public static String toJSONString(Object object) {
        return JsonObject.toJSONString(object);
    }
    public static <T> T parseObject(String json, Class<T> clz) {
        return JsonObject.parseObject(json, clz);
    }
}
