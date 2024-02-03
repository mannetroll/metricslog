package com.mannetroll.metrics.util;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author mannetroll
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("rawtypes")
    public static String toJson(Map map) {
        try {
            return mapper.writeValueAsString(map) + "\r\n";
        } catch (Exception e) {
            return "{\"" + LogKeys.EXCEPTION + "\":\"" + e.getMessage() + "\"}";
        }
    }

}
