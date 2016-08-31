package com.catena.web.interceptor;

import com.catena.core.CatenaContext;
import com.catena.mock.core.ScanUrlAndDataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by hx-pc on 16-5-17.
 */
public class MockUrlInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockUrlInterceptor.class);

    @Autowired
    private ScanUrlAndDataContext scanUrlAndDataContext;

    @Autowired
    private CatenaContext catenaContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuilder apiKey = new StringBuilder(request.getRequestURI());
        if (request.getParameterNames() != null && request.getParameterMap().size() > 0) {
            apiKey.append("?");
            for (Map.Entry<String,String[]> e : (request.getParameterMap()).entrySet()) {
                apiKey.append(e.getKey()).append("=").append(e.getValue()[0]).append("&");
            }
            apiKey = new StringBuilder(apiKey.substring(0,apiKey.length()-1));
        }
        LOGGER.info("请求 {}",apiKey);
        String data = scanUrlAndDataContext.getDataWithApi(apiKey.toString(), request.getMethod());
        if (!StringUtils.isEmpty(data)) {
            /*LOGGER.info("{}",catenaContext.getNodeOperationRepository());
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().write(data.toString().getBytes());*/
            request.setAttribute("data", data);
            catenaContext.getNodeOperationRepository().get("returnData").startReturnDataWithString(request, response);
        }
        return false;
    }

}
