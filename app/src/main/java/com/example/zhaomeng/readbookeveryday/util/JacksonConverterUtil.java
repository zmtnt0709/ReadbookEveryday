package com.example.zhaomeng.readbookeveryday.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;

/**
 * Jackson库解析.
 * <p/>
 * Created by zhangdaoqiang on 14/12/19.
 */
public class JacksonConverterUtil {
    private static JacksonConverterUtil instance;
    private final ObjectMapper objectMapper;

    public static JacksonConverterUtil getInstance() {
        if (instance == null) {
            synchronized (JacksonConverterUtil.class) {
                if (instance == null) {
                    instance = new JacksonConverterUtil();
                }
            }
        }
        return instance;
    }

    public JacksonConverterUtil() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }

    public String toJsonString(Object object) {
        if (object == null) return "";

        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public <T> T jsonStringToObject(String content, Class<T> clazz) {
        if (null != content) {
            try {
                return objectMapper.readValue(content, clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> T jsonStringToCollection(String jsonString, Class<? extends Collection> collectionClass, Class<?> elementClasses) {
        if (null == jsonString || jsonString.equals("")) return null;

        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
