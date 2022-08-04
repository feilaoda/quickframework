package com.quickpaas.framework.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonObject {
    private static final Logger log = LoggerFactory.getLogger(JsonObject.class);
    private JsonNode jsonNode;
    private JsonObject(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    private static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static String toJSONString(Object object) {
        try {
            return objectMapper().writeValueAsString(object);
        }catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return "null";
        }
    }
    public static <T> T parseObject(String json, Class<T> clz)  {
        if(json == null || json.equals("")) {
            return null;
        }
        try {
            return objectMapper().readValue(json, clz);
        }catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static JsonObject parseObject(String json)  {
        try {
            JsonNode jsonNode = objectMapper().readTree(json);
            return new JsonObject(jsonNode);
        }catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Integer getInteger(String name) {
        JsonNode node = jsonNode.get(name);
        if(node == null) {
            return null;
        }else {
            return node.asInt();
        }
    }

    public Long getLong(String name) {
        JsonNode node = jsonNode.get(name);
        if(node == null) {
            return null;
        }else {
            return node.asLong();
        }
    }

    public String getString(String name) {
        JsonNode node = jsonNode.get(name);
        if(node == null) {
            return null;
        }else {
            return node.asText();
        }
    }

    public JsonObject getJsonObject(String name) {
        JsonNode node = jsonNode.get(name);
        if(node == null) {
            return null;
        }else {
            return new JsonObject(node);
        }
    }

    public Map<String, Object> getMap(String name) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = jsonNode.get(name);
        if(node == null) {
            return new HashMap<>();
        }else {
            return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {
            });
        }
    }
}
