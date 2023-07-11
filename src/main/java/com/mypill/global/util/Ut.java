package com.mypill.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypill.global.AppConfig;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class Ut {

    private static ObjectMapper getObjectMapper() {
        return (ObjectMapper) AppConfig.getContext().getBean("objectMapper");
    }

    public static class json {

        public static String toStr(Object obj) {
            try {
                return getObjectMapper().writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    public static class url {

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (!url.contains("?")) {
                url += "?";
            }
            if (!url.endsWith("?") && !url.endsWith("&")) {
                url += "&";
            }
            url += paramName + "=" + paramValue;
            return url;
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);
            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;
            int endPoint = url.substring(startPoint).indexOf("&");
            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }
            String urlAfter = url.substring(startPoint + endPoint + 1);
            return url.substring(0, startPoint) + urlAfter;
        }

        public static String encode(String str) {
            return URLEncoder.encode(str, StandardCharsets.UTF_8);
        }
    }
}