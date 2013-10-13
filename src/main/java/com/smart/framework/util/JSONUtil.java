package com.smart.framework.util;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtil {

    private static final Logger logger = Logger.getLogger(JSONUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 将 Java 对象转为 JSON 字符串
    public static <T> String toJSON(T obj) {
        String jsonStr;
        try {
            jsonStr = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("Java 转 JSON 出错！", e);
            throw new RuntimeException(e);
        }
        return jsonStr;
    }

    // 将 JSON 字符串转为 Java 对象
    public static <T> T fromJSON(String json, Class<T> type) {
        T obj;
        try {
            obj = objectMapper.readValue(json, type);
        } catch (Exception e) {
            logger.error("JSON 转 Java 出错！", e);
            throw new RuntimeException(e);
        }
        return obj;
    }
}
