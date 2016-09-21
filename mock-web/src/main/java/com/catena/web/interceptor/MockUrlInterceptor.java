package com.catena.web.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by hx-pc on 16-5-17.
 */
public class MockUrlInterceptor extends HandlerInterceptorAdapter {

    protected StringBuilder getUrlAddress(HttpServletRequest request) {
        StringBuilder apiKey = new StringBuilder(request.getRequestURI());
        if (request.getParameterNames() != null && request.getParameterMap().size() > 0) {
            apiKey.append("?");
            for (Map.Entry<String, String[]> e : (request.getParameterMap()).entrySet()) {
                apiKey.append(e.getKey()).append("=").append(e.getValue()[0]).append("&");
            }
            apiKey = new StringBuilder(apiKey.substring(0, apiKey.length() - 1));
        }
        return apiKey;
    }

}
